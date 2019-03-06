package com.wsc.redisinaction.chapter06;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

public class DelayedTasks {
    public static final void main(String[] args)
        throws Exception
    {
        new DelayedTasks().run();
    }

    public void run()
        throws InterruptedException, IOException
    {
        Jedis conn = new Jedis("localhost");
        conn.select(15);
        testDelayedTasks(conn);
    }
    public void testDelayedTasks(Jedis conn)
            throws InterruptedException
        {
            System.out.println("\n----- testDelayedTasks -----");
            conn.del("queue:tqueue", "delayed:");
            System.out.println("Let's start some regular and delayed tasks...");
            for (long delay : new long[]{0, 500, 0, 1500}){
                assert executeLater(conn, "tqueue", "testfn", new ArrayList<String>(), delay) != null;
            }
            long r = conn.llen("queue:tqueue");
            System.out.println("How many non-delayed tasks are there (should be 2)? " + r);
            assert r == 2;
            System.out.println();

            System.out.println("Let's start up a thread to bring those delayed tasks back...");
            PollQueueThread thread = new PollQueueThread();
            thread.start();
            System.out.println("Started.");
            System.out.println("Let's wait for those tasks to be prepared...");
            Thread.sleep(2000);
            thread.quit();
            thread.join();
            r = conn.llen("queue:tqueue");
            System.out.println("Waiting is over, how many tasks do we have (should be 4)? " + r);
            assert r == 4;
            conn.del("queue:tqueue", "delayed:");
        }
    
    /**
     * �����ӳ�����
     * 
     * page 137
     * 
     * �嵥 6-22
     * 
     * @param conn
     * @param queue
     * @param name
     * @param args
     * @param delay
     * @return
     */
    public String executeLater(
        Jedis conn, String queue, String name, List<String> args, long delay)
    {
    	//Josn�б�
        Gson gson = new Gson();
        
        //����Ψһ�ı�ʶ��
        String identifier = UUID.randomUUID().toString();
        
        //׼������Ҫ��ӵ�����
        String itemArgs = gson.toJson(args);
        String item = gson.toJson(new String[]{identifier, queue, name, itemArgs});
        
        if (delay > 0){
        	//�ӳ���������ִ��ʱ��
            conn.zadd("delayed:", System.currentTimeMillis() + delay, item);
        } else {
        	//����ִ���������
        	conn.rpush("queue:" + queue, item);
        }
        //���ر�ʶ��
        return identifier;
    }

    public String acquireLock(Jedis conn, String lockName) {
        return acquireLock(conn, lockName, 10000);
    }
    /**
     * ʹ��������ķ������Ի���������ʧ�����³��ԡ�ֱ���ɹ�
     * 
     * page 119
     * �嵥 6-8
     * 
     * @param conn
     * @param lockName
     * @param acquireTimeout
     * @return
     */
    public String acquireLock(Jedis conn, String lockName, long acquireTimeout){
    	//128λ�����ʶ��
        String identifier = UUID.randomUUID().toString();

        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end){
            //����ȡ����
        	//setnx��������ã������ڴ������ļ������ڵ�����£�Ϊ������һ��ֵ���Դ��������
        	if (conn.setnx("lock:" + lockName, identifier) == 1){
                return identifier;
            }

            try {
                Thread.sleep(1);
            }catch(InterruptedException ie){
                Thread.currentThread().interrupt();
            }
        }

        return null;
    }
    
    /**
     * �ͷ�������ʹ��watch���Ӵ������ļ������ż���Ŀǰ��ֵ�Ƿ�ͼ���ʱ���õ�һ����
     * ����ȷ��ֵû�з����仯֮��ɾ���ü�
     * 
     * page 120
     * �嵥 6-9
     * 
     * @param conn
     * @param lockName
     * @param identifier
     * @return
     */
    public boolean releaseLock(Jedis conn, String lockName, String identifier) {
        String lockKey = "lock:" + lockName;

        while (true){
            conn.watch(lockKey);
            
            //�������Ƿ���Ȼ������
            if (identifier.equals(conn.get(lockKey))){
                Transaction trans = conn.multi();
                //ɾ���ü����ͷ���
                trans.del(lockKey);
                List<Object> results = trans.exec();
                if (results == null){
                    continue;
                }
                return true;
            }

            conn.unwatch();
            break;
        }
        
        //�����Ѿ�ʧȥ����
        return false;
    }
    
    /**
     * ���ӳٶ����л�ȡ��ִ������
     * 
     * page 138
     * 
     * 
     * @author wsc
     *
     */
    public class PollQueueThread
        extends Thread
    {
        private Jedis conn;
        private boolean quit;
        private Gson gson = new Gson();

        public PollQueueThread(){
            this.conn = new Jedis("localhost");
            this.conn.select(15);
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            while (!quit){
            	//��ȡ��������еĵ�һ������
                Set<Tuple> items = conn.zrangeWithScores("delayed:", 0, 0);
                Tuple item = items.size() > 0 ? items.iterator().next() : null;
                
                //������û�а����κ����񣬻��������ִ��ʱ��δ��
                if (item == null || item.getScore() > System.currentTimeMillis()) {
                    try{
                        sleep(10);
                    }catch(InterruptedException ie){
                        Thread.interrupted();
                    }
                    continue;
                }
                
                //����Ҫ��ִ�е�����Ū�����Ӧ�ñ������ĸ�����Ķ���
                String json = item.getElement();
                String[] values = gson.fromJson(json, String[].class);
                String identifier = values[0];
                String queue = values[1];
                
                //��ȡ��ʧ�ܣ���������Ĳ��貢����
                String locked = acquireLock(conn, identifier);
                if (locked == null){
                    continue;
                }
                
                //�����������ʵ��������������
                if (conn.zrem("delayed:", json) == 1){
                    conn.rpush("queue:" + queue, json);
                }
                
                //�ͷ���
                releaseLock(conn, identifier, locked);
            }
        }
    }

}
