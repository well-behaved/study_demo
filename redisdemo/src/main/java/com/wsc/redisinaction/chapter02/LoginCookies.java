package com.wsc.redisinaction.chapter02;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import redis.clients.jedis.Jedis;

public class LoginCookies {
	public static void main(String[] args) 
			throws InterruptedException
	{
		new LoginCookies().run();
	}
	
	public void run() 
			throws InterruptedException
	{	
		Jedis conn = new Jedis("localhost");
        conn.select(15);
        
        testLoginCookies(conn);
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
}
