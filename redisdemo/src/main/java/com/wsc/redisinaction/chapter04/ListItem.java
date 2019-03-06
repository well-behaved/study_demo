package com.wsc.redisinaction.chapter04;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

public class ListItem {
    public static final void main(String[] args) {
        new ListItem().run();
    }

    public void run() {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        testListItem(conn, false);
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

}
