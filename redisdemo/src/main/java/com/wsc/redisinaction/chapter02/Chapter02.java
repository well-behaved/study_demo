package com.wsc.redisinaction.chapter02;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Chapter02 {
    public static final void main(String[] args)
        throws InterruptedException
    {
        new Chapter02().run();
    }

    public void run()
        throws InterruptedException
    {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        testLoginCookies(conn);
        testShopppingCartCookies(conn);
        testCacheRows(conn);
        testCacheRequest(conn);
    }

    public void testLoginCookies(Jedis conn)
        throws InterruptedException
    {
        System.out.println("\n----- testLoginCookies -----");
        String token = UUID.randomUUID().toString();

        updateToken(conn, token, "username", "itemX");
        System.out.println("We just logged-in/updated token: " + token);
        System.out.println("For user: 'username'");
        System.out.println();

        System.out.println("What username do we get when we look-up that token?");
        String r = checkToken(conn, token);
        System.out.println(r);
        System.out.println();
        assert r != null;

        System.out.println("Let's drop the maximum number of cookies to 0 to clean them out");
        System.out.println("We will start a thread to do the cleaning, while we stop it later");

        CleanSessionsThread thread = new CleanSessionsThread(0);
        thread.start();
        Thread.sleep(1000);
        thread.quit();
        Thread.sleep(2000);
        if (thread.isAlive()){
            throw new RuntimeException("The clean sessions thread is still alive?!?");
        }

        long s = conn.hlen("login:");
        System.out.println("The current number of sessions still available is: " + s);
        assert s == 0;
    }

    public void testShopppingCartCookies(Jedis conn)
        throws InterruptedException
    {
        System.out.println("\n----- testShopppingCartCookies -----");
        String token = UUID.randomUUID().toString();

        System.out.println("We'll refresh our session...");
        updateToken(conn, token, "username", "itemX");
        System.out.println("And add an item to the shopping cart");
        addToCart(conn, token, "itemY", 3);
        Map<String,String> r = conn.hgetAll("cart:" + token);
        System.out.println("Our shopping cart currently has:");
        for (Map.Entry<String,String> entry : r.entrySet()){
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();

        assert r.size() >= 1;

        System.out.println("Let's clean out our sessions and carts");
        CleanFullSessionsThread thread = new CleanFullSessionsThread(0);
        thread.start();
        Thread.sleep(1000);
        thread.quit();
        Thread.sleep(2000);
        if (thread.isAlive()){
            throw new RuntimeException("The clean sessions thread is still alive?!?");
        }

        r = conn.hgetAll("cart:" + token);
        System.out.println("Our shopping cart now contains:");
        for (Map.Entry<String,String> entry : r.entrySet()){
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        assert r.size() == 0;
    }

    public void testCacheRows(Jedis conn)
        throws InterruptedException
    {
        System.out.println("\n----- testCacheRows -----");
        System.out.println("First, let's schedule caching of itemX every 5 seconds");
        scheduleRowCache(conn, "itemX", 5);
        System.out.println("Our schedule looks like:");
        Set<Tuple> s = conn.zrangeWithScores("schedule:", 0, -1);
        for (Tuple tuple : s){
            System.out.println("  " + tuple.getElement() + ", " + tuple.getScore());
        }
        assert s.size() != 0;

        System.out.println("We'll start a caching thread that will cache the data...");

        CacheRowsThread thread = new CacheRowsThread();
        thread.start();

        Thread.sleep(1000);
        System.out.println("Our cached data looks like:");
        String r = conn.get("inv:itemX");
        System.out.println(r);
        assert r != null;
        System.out.println();

        System.out.println("We'll check again in 5 seconds...");
        Thread.sleep(5000);
        System.out.println("Notice that the data has changed...");
        String r2 = conn.get("inv:itemX");
        System.out.println(r2);
        System.out.println();
        assert r2 != null;
        assert !r.equals(r2);

        System.out.println("Let's force un-caching");
        scheduleRowCache(conn, "itemX", -1);
        Thread.sleep(1000);
        r = conn.get("inv:itemX");
        System.out.println("The cache was cleared? " + (r == null));
        assert r == null;

        thread.quit();
        Thread.sleep(2000);
        if (thread.isAlive()){
            throw new RuntimeException("The database caching thread is still alive?!?");
        }
    }

    public void testCacheRequest(Jedis conn) {
        System.out.println("\n----- testCacheRequest -----");
        String token = UUID.randomUUID().toString();

        Callback callback = new Callback(){
            public String call(String request){
                return "content for " + request;
            }
        };

        updateToken(conn, token, "username", "itemX");
        String url = "http://test.com/?item=itemX";
        System.out.println("We are going to cache a simple request against " + url);
        String result = cacheRequest(conn, url, callback);
        System.out.println("We got initial content:\n" + result);
        System.out.println();

        assert result != null;

        System.out.println("To test that we've cached the request, we'll pass a bad callback");
        String result2 = cacheRequest(conn, url, null);
        System.out.println("We ended up getting the same response!\n" + result2);

        assert result.equals(result2);

        assert !canCache(conn, "http://test.com/");
        assert !canCache(conn, "http://test.com/?item=itemX&_=1234536");
    }

    	
    /**
     * ����¼cookie
     * 
     * page 25
     * �嵥 2-1
     * 
     * @param conn
     * @param token
     * @return
     */
    public String checkToken(Jedis conn, String token) {
    	//���Ի�ȡ������������Ӧ���û�
        return conn.hget("login:", token);
    }

    /**
     * �����������
     * 
     * page 25
     * �嵥2-2
     * 
     * @param conn
     * @param token
     * @param user
     * @param item
     */
    public void updateToken(Jedis conn, String token, String user, String item) {
    	//��ȡ��ǰ��ʱ���
        long timestamp = System.currentTimeMillis() / 1000;
        
        //ά���������Ե�¼�û�֮���ӳ��
        conn.hset("login:", token, user);
        
        //��¼�������һ�γ��ֵ�ʱ��
        conn.zadd("recent:", timestamp, token);
        
        if (item != null) {
        	//��¼�û����������Ʒ
        	conn.zadd("viewed:" + token, timestamp, item);
            
        	//�Ƴ��ɵļ�¼��ֻ�����û�����������25����Ʒ
        	conn.zremrangeByRank("viewed:" + token, 0, -26);
            conn.zincrby("viewed:", -1, item);
        }
    }

    /**
     * ���¹��ﳵ
     * 
     * page 28
     * �嵥 2-4
     * 
     * @param conn
     * @param session
     * @param item
     * @param count
     */
    public void addToCart(Jedis conn, String session, String item, int count) {
        if (count <= 0) {
        	//�ӹ��ﳵ���Ƴ�ָ������Ʒ
            conn.hdel("cart:" + session, item);
        } else {
        	//��ָ������Ʒ��ӵ����ﳵ��
            conn.hset("cart:" + session, item, String.valueOf(count));
        }
    }

    /**
     * ������Ȼ������ֹ����ĺ���
     * 
     * page 32
     * �嵥 2-7
     * 
     * 
     * @param conn
     * @param rowId
     * @param delay
     */
    public void scheduleRowCache(Jedis conn, String rowId, int delay) {
    	//�����������е��ӳ�ֵ
        conn.zadd("delay:", delay, rowId);
        
        //��������Ҫ����������н��е���
        conn.zadd("schedule:", System.currentTimeMillis() / 1000, rowId);
    }

    /**
     * 
     * ҳ�滺�溯��
     * 
     * page 30
     *  �嵥 2-6
     * 
     * 
     * @param conn
     * @param request
     * @param callback
     * @return
     */
    public String cacheRequest(Jedis conn, String request, Callback callback) {
    	//���ڲ���ֱ�ӱ����������ֱ�ӵ��ûص�����
        if (!canCache(conn, request)){
            return callback != null ? callback.call(request) : null;
        }
        
        //������ת��һ���򵥵��ַ������������Ժ���в���
        String pageKey = "cache:" + hashRequest(request);
        
        //���Բ��ұ������ҳ��
        String content = conn.get(pageKey);

        if (content == null && callback != null){
            //���ҳ�滹û�б����棬��ô����ҳ��
        	content = callback.call(request);

        	//�������ɵ�ҳ��ŵ�����ҳ����
        	conn.setex(pageKey, 300, content);
        }

        return content;
    }

    /**
     * �Ƿ���Ҫ������
     * 
     * page 34
     * �嵵 2-11
     * 
     * @param conn   Jedis
     * @param request �Ƿ��ϻ����ҳ��
     * @return  
     */
    public boolean canCache(Jedis conn, String request) {
        try {
            URL url = new URL(request);
            HashMap<String,String> params = new HashMap<String,String>();
            if (url.getQuery() != null){
                for (String param : url.getQuery().split("&")){
                    String[] pair = param.split("=", 2);
                    params.put(pair[0], pair.length == 2 ? pair[1] : null);
                }
            }

            //���Գ�ҳ������ȡ����ƷID
            String itemId = extractItemId(params);
            
            //������ҳ���ܷ񱻻����Լ����ҳ���Ƿ�Ϊ��Ʒҳ��
            if (itemId == null || isDynamic(params)) {
                return false;
            }
            
            //ȡ�������Ʒ�������������
            Long rank = conn.zrank("viewed:", itemId);
            
            //������Ʒ����������������ж��Ƿ���Ҫ�������ҳ��
            return rank != null && rank < 10000;
        }catch(MalformedURLException mue){
            return false;
        }
    }
    
    
    /**
     * ҳ���Ƿ�����Ʒ��ҳ��
     * 
     * ��������
     * 
     * 
     * @param params
     * @return
     */
    public boolean isDynamic(Map<String,String> params) {
        return params.containsKey("_");
    }

    /**
     * ���Գ�ҳ������ȡ����ƷID
     * 
     * ��������
     * 
     * @param params
     * @return
     */
    public String extractItemId(Map<String,String> params) {
        return params.get("item");
    }

    
    /**
     * ������ת��Ϊһ��hash��
     * 
     * ��������
     * 
     * @param request
     * @return
     */
    public String hashRequest(String request) {
        return String.valueOf(request.hashCode());
    }

    /**
     * �ص�����
     * 
     * @author wsc
     *
     */
    public interface Callback {
        public String call(String request);
    }

    
    /**
     * 
     * �����̣߳���ʱ����ͻỰ
     * @author wsc
     *
     */
    public class CleanSessionsThread
        extends Thread
    {
        private Jedis conn;
        private int limit;
        private boolean quit;

        public CleanSessionsThread(int limit) {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
            this.limit = limit;
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            while (!quit) {
            	//�ҳ�Ŀǰ�������Ƶ�����
                long size = conn.zcard("recent:");
                
                //���Ƶ�����δ�������ƣ����߲���֮�����¼��
                if (size <= limit){
                    try {
                        sleep(1000);
                    }catch(InterruptedException ie){
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                //��ȡ��Ҫ�Ƴ�������ID
                long endIndex = Math.min(size - limit, 100);
                Set<String> tokenSet = conn.zrange("recent:", 0, endIndex - 1);
                String[] tokens = tokenSet.toArray(new String[tokenSet.size()]);

                //Ϊ��Щ��Ҫ��ɾ�������ƹ�������
                ArrayList<String> sessionKeys = new ArrayList<String>();
                for (String token : tokens) {
                    sessionKeys.add("viewed:" + token);
                }

                //�Ƴ���ɵ���Щ����
                conn.del(sessionKeys.toArray(new String[sessionKeys.size()]));
                conn.hdel("login:", tokens);
                conn.zrem("recent:", tokens);
            }
        }
    }

    
    
    /**
     * �̶߳�ʱ���£�������ɵĻỰʱ�����ɻỰ��Ӧ�Ĺ��ﳵҲһ��ɾ��
     * 
     * @author wsc
     *
     */
    public class CleanFullSessionsThread
        extends Thread
    {
        private Jedis conn;
        private int limit;
        private boolean quit;

        public CleanFullSessionsThread(int limit) {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
            this.limit = limit;
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            while (!quit) {
            	//�ҳ�Ŀǰ�������Ƶ�����
                long size = conn.zcard("recent:");
                
                //���Ƶ�����δ�������ƣ����߲���֮�����¼��
                if (size <= limit){
                    try {
                        sleep(1000);
                    }catch(InterruptedException ie){
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                //��ȡ��Ҫ�Ƴ�������ID
                long endIndex = Math.min(size - limit, 100);
                Set<String> sessionSet = conn.zrange("recent:", 0, endIndex - 1);
                String[] sessions = sessionSet.toArray(new String[sessionSet.size()]);

                //Ϊ��Щ��Ҫ��ɾ�������ƹ�������
                ArrayList<String> sessionKeys = new ArrayList<String>();
                for (String sess : sessions) {
                    sessionKeys.add("viewed:" + sess);
                    
                    //����ɾ���ɻỰ��Ӧ�õĹ��ﳵ
                    sessionKeys.add("cart:" + sess);
                }

                //�Ƴ���ɵ���Щ����
                conn.del(sessionKeys.toArray(new String[sessionKeys.size()]));
                conn.hdel("login:", sessions);
                conn.zrem("recent:", sessions);
            }
        }
    }

    /**
     * �ػ�������
     * 
     * page 32 
     * �嵥 2-8
     * 
     * 
     * @author wsc
     *
     */
    public class CacheRowsThread
        extends Thread
    {
        private Jedis conn;
        private boolean quit;

        public CacheRowsThread() {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
        }

        public void quit() {
            quit = true;
        }

        public void run() {
        	
        	//Josn��ʽ������
            Gson gson = new Gson();
            while (!quit){
                Set<Tuple> range = conn.zrangeWithScores("schedule:", 0, 0);
                
                //���Ի�ȡ��һ����Ҫ��������������Լ����еĵ���ʱ�����
                //����᷵��һ�����������һ��Ԫ�飨Tuple�����б�
                Tuple next = range.size() > 0 ? range.iterator().next() : null;
                long now = System.currentTimeMillis() / 1000;
                if (next == null || next.getScore() > now){
                    try {
                    	//��ʱû������Ҫ�����棬����50���������
                        sleep(50);
                    }catch(InterruptedException ie){
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
                
                String rowId = next.getElement();
                
                //��ǰ��ȡ��һ�ε��ȵ��ӳ�ʱ��
                double delay = conn.zscore("delay:", rowId);
                
                //�����ڻ�������У������ӻ������Ƴ�
                if (delay <= 0) {
                    conn.zrem("delay:", rowId);
                    conn.zrem("schedule:", rowId);
                    conn.del("inv:" + rowId);
                    continue;
                }
                
                //��ȡ������
                Inventory row = Inventory.get(rowId);
                
                //���µ���ʱ�䣬�����û���ֵ
                conn.zadd("schedule:", now + delay, rowId);
                conn.set("inv:" + rowId, gson.toJson(row));
            }
        }
    }

    public static class Inventory {
        @SuppressWarnings("unused")
		private String id;
        @SuppressWarnings("unused")
		private String data;
        @SuppressWarnings("unused")
		private long time;

        private Inventory (String id) {
            this.id = id;
            this.data = "data to cache...";
            this.time = System.currentTimeMillis() / 1000;
        }

        public static Inventory get(String id) {
            return new Inventory(id);
        }
    }
}
