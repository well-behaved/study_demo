package com.wsc.redisinaction.chapter02;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import redis.clients.jedis.Jedis;

public class ShopppingCartCookies {

	
	public static void main(String[] args) 
			throws InterruptedException
	{
		new ShopppingCartCookies().run();
	}
	
	public void run() 
			throws InterruptedException
	{	
		Jedis conn = new Jedis("localhost");
        conn.select(15);
        
        testShopppingCartCookies(conn);
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

}
