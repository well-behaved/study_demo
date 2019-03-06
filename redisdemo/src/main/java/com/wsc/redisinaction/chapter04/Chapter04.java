package com.wsc.redisinaction.chapter04;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Chapter04 {
    public static final void main(String[] args) {
        new Chapter04().run();
    }
	
    public void run() {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        testListItem(conn, false);
        testPurchaseItem(conn);
        testBenchmarkUpdateToken(conn);
    }

    public void testListItem(Jedis conn, boolean nested) {
        if (!nested){
            System.out.println("\n----- testListItem -----");
        }

        System.out.println("We need to set up just enough state so that a user can list an item");
        String seller = "userX";
        String item = "itemX";
        conn.sadd("inventory:" + seller, item);
        Set<String> i = conn.smembers("inventory:" + seller);

        System.out.println("The user's inventory has:");
        for (String member : i){
            System.out.println("  " + member);
        }
        assert i.size() > 0;
        System.out.println();

        System.out.println("Listing the item...");
        boolean l = listItem(conn, item, seller, 10);
        System.out.println("Listing the item succeeded? " + l);
        assert l;
        Set<Tuple> r = conn.zrangeWithScores("market:", 0, -1);
        System.out.println("The market contains:");
        for (Tuple tuple : r){
            System.out.println("  " + tuple.getElement() + ", " + tuple.getScore());
        }
        assert r.size() > 0;
    }

    public void testPurchaseItem(Jedis conn) {
        System.out.println("\n----- testPurchaseItem -----");
        testListItem(conn, true);

        System.out.println("We need to set up just enough state so a user can buy an item");
        conn.hset("users:userY", "funds", "125");
        Map<String,String> r = conn.hgetAll("users:userY");
        System.out.println("The user has some money:");
        for (Map.Entry<String,String> entry : r.entrySet()){
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        assert r.size() > 0;
        assert r.get("funds") != null;
        System.out.println();

        System.out.println("Let's purchase an item");
        boolean p = purchaseItem(conn, "userY", "itemX", "userX", 10);
        System.out.println("Purchasing an item succeeded? " + p);
        assert p;
        r = conn.hgetAll("users:userY");
        System.out.println("Their money is now:");
        for (Map.Entry<String,String> entry : r.entrySet()){
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        assert r.size() > 0;

        String buyer = "userY";
        Set<String> i = conn.smembers("inventory:" + buyer);
        System.out.println("Their inventory is now:");
        for (String member : i){
            System.out.println("  " + member);
        }
        assert i.size() > 0;
        assert i.contains("itemX");
        assert conn.zscore("market:", "itemX.userX") == null;
    }

    public void testBenchmarkUpdateToken(Jedis conn) {
        System.out.println("\n----- testBenchmarkUpdate -----");
        benchmarkUpdateToken(conn, 5);
    }

    /**
     * 
     * ���ӣ����۵���Ʒ
     * 
     * page 78
     * �����嵥 4-5
     * 
     * 
     * @param conn
     * @param itemId
     * @param sellerId
     * @param price
     * @return
     */
    public boolean listItem(
            Jedis conn, String itemId, String sellerId, double price) {

        String inventory = "inventory:" + sellerId;
        String item = itemId + '.' + sellerId;
        long end = System.currentTimeMillis() + 5000;

        while (System.currentTimeMillis() < end) {
        	//�����û����������ı仯
            conn.watch(inventory);
            
            //����û��Ƿ���Ȼ���н�Ҫ�����۵���Ʒ
            if (!conn.sismember(inventory, itemId)){
                //���ָ������Ʒ�����û��������棬��ôֹͣ�԰������ļ��Ӳ�����һ����ֵ
            	conn.unwatch();
                return false;
            }

            //�ѱ����۵���Ʒ��ӵ���Ʒ�����г�����
            Transaction trans = conn.multi();
            trans.zadd("market:", price, item);
            trans.srem(inventory, itemId);
            
            //���ִ��exec����û������watchError�쳣
            //��ô˵�������Ѿ�ִ�гɹ������Ҷ԰������ļ���Ҳ����
            List<Object> results = trans.exec();
            // null response indicates that the transaction was aborted due to
            // the watched key changing.
            if (results == null){
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * ���г��Ϲ���һ����Ʒ��֧���빺��Ĺ���
     * 
     * page 80
     * �����嵥 4-6
     * 
     * 
     * @param conn
     * @param buyerId
     * @param itemId
     * @param sellerId
     * @param lprice
     * @return
     */
    public boolean purchaseItem(
            Jedis conn, String buyerId, String itemId, String sellerId, double lprice) {

        String buyer = "users:" + buyerId;
        String seller = "users:" + sellerId;
        String item = itemId + '.' + sellerId;
        String inventory = "inventory:" + buyerId;
        long end = System.currentTimeMillis() + 10000;

        while (System.currentTimeMillis() < end){
        	
        	//����Ʒ�����г��Լ���ҵĸ�����Ϣ���м��
            conn.watch("market:", buyer);

            //��������Ҫ�������Ʒ�ļ۸��Ƿ����仯���Լ�����Ƿ����㹻��Ǯ�����������Ʒ
            double price = conn.zscore("market:", item);
            double funds = Double.parseDouble(conn.hget(buyer, "funds"));
            if (price != lprice || price > funds){
                conn.unwatch();
                return false;
            }

            //����������ƣ��Ƚ����֧����Ǯת�Ƶ����ң�Ȼ�󽫱��������Ʒ�ƽ������
            Transaction trans = conn.multi();
            trans.hincrBy(seller, "funds", (int)price);
            trans.hincrBy(buyer, "funds", (int)-price);
            trans.sadd(inventory, itemId);
            trans.zrem("market:", item);
            List<Object> results = trans.exec();
            // null response indicates that the transaction was aborted due to
            // the watched key changing.
            //������׵Ĺ����з������⣬��ô�������ԣ���ΪresultsΪ�մ�����������ʧ�ܣ�������һ����ֵ
            if (results == null){
                continue;
            }
            return true;
        }

        return false;
    }

    /**
     *���ܲ��Ժ������ڸ�����ʱ�����ظ�ִ�� updateToken������updateTokenPipeline 
     *Ȼ����㱻���Եķ�������ִ���˶��ٴ�
     *
     *page 84
     *�嵥 4-9
     * 
     * 
     * @param conn
     * @param duration
     */
    public void benchmarkUpdateToken(Jedis conn, int duration) {
        try{
            @SuppressWarnings("rawtypes")
            Class[] args = new Class[]{
                Jedis.class, String.class, String.class, String.class};
            Method[] methods = new Method[]{
                this.getClass().getDeclaredMethod("updateToken", args),
                this.getClass().getDeclaredMethod("updateTokenPipeline", args),
            };
            
            //���Ի�ֱ�ִ��updateToken������updateTokenPipeline����
            for (Method method : methods){
            	
            	//���ü������Լ����Խ���������
                int count = 0;
                long start = System.currentTimeMillis();
                long end = start + (duration * 1000);
                while (System.currentTimeMillis() < end){
                    count++;
                    //�������������е�һ��
                    method.invoke(this, conn, "token", "user", "item");
                }
                
                //���㷽����ִ��ʱ��
                long delta = System.currentTimeMillis() - start;
                
                //��ӡ���Խ��
                System.out.println(
                        method.getName() + ' ' +
                        count + ' ' +
                        (delta / 1000) + ' ' +
                        (count / (delta / 1000)));
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * �����¼�û�������������Ʒ�Լ�������ʹ���ҳ��
     * 
     * page 83
     * �嵥 4-7
     * 
     * 
     * @param conn
     * @param token
     * @param user
     * @param item
     */
    public void updateToken(Jedis conn, String token, String user, String item) {
    	//��ȡʱ���
        long timestamp = System.currentTimeMillis() / 1000;
        
        //�����������ѵ�¼�û�֮���ӳ��
        conn.hset("login:", token, user);
        
        //��¼���������ֵ�ʱ��
        conn.zadd("recent:", timestamp, token);
       
        if (item != null) {
        	//���û����������Ʒ��¼����
        	conn.zadd("viewed:" + token, timestamp, item);
            
        	//�Ƴ�����Ʒ��ֻ��¼���������25����Ʒ
        	conn.zremrangeByRank("viewed:" + token, 0, -26);
            
        	//���¸�����Ʒ�ı��������
        	conn.zincrby("viewed:", -1, item);
        }
    }

    /**
     * ����һ������������ˮ�ߣ�Ȼ��ʹ����ˮ�����������е�����
     * 
     * page 84
     * 
     * �嵥 4-8
     * 
     * @param conn
     * @param token
     * @param user
     * @param item
     */
    public void updateTokenPipeline(Jedis conn, String token, String user, String item) {
        long timestamp = System.currentTimeMillis() / 1000;
        
        //������ˮ��
        Pipeline pipe = conn.pipelined();
        pipe.hset("login:", token, user);
        pipe.zadd("recent:", timestamp, token);
        if (item != null){
            pipe.zadd("viewed:" + token, timestamp, item);
            pipe.zremrangeByRank("viewed:" + token, 0, -26);
            pipe.zincrby("viewed:", -1, item);
        }
        
        //ִ����Щ����ˮ�߰���������
        pipe.exec();
    }
}
