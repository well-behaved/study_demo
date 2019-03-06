package com.wsc.redisinaction.chapter06;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.ZParams;

public class CountingSemaphore {
	public static void main(String[] args) 
			throws Exception
	{
			 new CountingSemaphore().run();
	}
    public void run()
        	throws InterruptedException, IOException
    {
        	Jedis conn = new Jedis("localhost");
        	conn.select(15);
        	testCountingSemaphore(conn);       
    }
    
    public void testCountingSemaphore(Jedis conn)
            throws InterruptedException
    {
            System.out.println("\n----- testCountingSemaphore -----");
            conn.del("testsem", "testsem:owner", "testsem:counter");
            System.out.println("Getting 3 initial semaphores with a limit of 3...");
            for (int i = 0; i < 3; i++) {
                assert acquireFairSemaphore(conn, "testsem", 3, 1000) != null;
            }
            System.out.println("Done!");
            System.out.println("Getting one more that should fail...");
            assert acquireFairSemaphore(conn, "testsem", 3, 1000) == null;
            System.out.println("Couldn't get it!");
            System.out.println();

            System.out.println("Lets's wait for some of them to time out");
            Thread.sleep(2000);
            System.out.println("Can we get one?");
            String id = acquireFairSemaphore(conn, "testsem", 3, 1000);
            assert id != null;
            System.out.println("Got one!");
            System.out.println("Let's release it...");
            assert releaseFairSemaphore(conn, "testsem", id);
            System.out.println("Released!");
            System.out.println();
            System.out.println("And let's make sure we can get 3 more!");
            for (int i = 0; i < 3; i++) {
                assert acquireFairSemaphore(conn, "testsem", 3, 1000) != null;
            }
            System.out.println("We got them!");
            conn.del("testsem", "testsem:owner", "testsem:counter");
    }    
    
    /**
     * ��ƽ�ź����Ļ�ȡ���Ƚϸ��ӡ�
     * �������򼯺��Ƴ���ʱ�ź�����Ȼ��ʱ�������ź���ӵ���߼��ϲ�������
     * ֮��Լ��������������������������ɵ�ֵ��ӵ��ź���ӵ�������򼯺�
     * 
     * page 129
     * �嵥 6-114
     * 
     * @param conn
     * @param semname
     * @param limit
     * @param timeout
     * @return
     */
    public String acquireFairSemaphore(
        Jedis conn, String semname, int limit, long timeout)
    {
    	//128λ�����ʶ��
        String identifier = UUID.randomUUID().toString();
        String czset = semname + ":owner";
        String ctr = semname + ":counter";

        long now = System.currentTimeMillis();
        Transaction trans = conn.multi();
        //ɾ����ʱ���ź���
        trans.zremrangeByScore(
            semname.getBytes(),
            "-inf".getBytes(),
            String.valueOf(now - timeout).getBytes());
        
        ZParams params = new ZParams();
        params.weights(1, 0);
        //��ʱ���򼯺����ź���ӵ�������򼯺�ִ�в������㣬
        //�������������浽�ź���ӵ�������򼯺�����
        //�������򼯺�ԭ�е�����
        trans.zinterstore(czset, params, czset, semname);
        //�Լ�����ִ����������������ȡ��������ִ����������֮���ֵ
        trans.incr(ctr);
        List<Object> results = trans.exec();
        int counter = ((Long)results.get(results.size() - 1)).intValue();

        trans = conn.multi();
        //���Ի�ȡ�ź���
        trans.zadd(semname, now, identifier);
        trans.zadd(czset, counter, identifier);
        
        //ͨ������������жϿͻ����Ƿ�ȡ�����ź���
        trans.zrank(czset, identifier);
        results = trans.exec();
        int result = ((Long)results.get(results.size() - 1)).intValue();
        //����Ƿ�ɹ���ȡ�����ź���
        if (result < limit){
            return identifier;
        }

        trans = conn.multi();
        //��ȡ�ź���ʧ�ܣ�ɾ��֮ǰ��ӵı�ʶ��
        trans.zrem(semname, identifier);
        trans.zrem(czset, identifier);
        trans.exec();
        return null;
    }

    /**
     * �ź������ͷţ�����ֻ��Ҫ�����򼯺������Ƴ�ָ���ı�ʶ��
     * ����ź����Ѿ�����ȷ���ͷŵ�����ô����true��
     *����false���ʾ���ź����Ѿ���Ϊ���ڶ���ɾ����
     * 
     * page 128
     * �嵥6-13
     * 
     * @param conn
     * @param semname
     * @param identifier
     * @return
     */
    public boolean releaseFairSemaphore(
        Jedis conn, String semname, String identifier)
    {
    	//�ź������ͷţ�����ֻ��Ҫ�����򼯺������Ƴ�ָ���ı�ʶ��
        Transaction trans = conn.multi();
        trans.zrem(semname, identifier);
        trans.zrem(semname + ":owner", identifier);
        List<Object> results = trans.exec();
        
        //����ź����Ѿ�����ȷ���ͷŵ�����ô����true��
        //����false���ʾ���ź����Ѿ���Ϊ���ڶ���ɾ����
        return (Long)results.get(results.size() - 1) == 1;
    }
}
