package com.wsc.redisinaction.chapter06;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class DistributedLocking {
	public static void main(String[] args) 
		throws Exception
	{
		 new DistributedLocking().run();
	}
    public void run()
    	throws InterruptedException, IOException
    {
    	Jedis conn = new Jedis("localhost");
    	conn.select(15);
    	testDistributedLocking(conn);       
    }
    
    /**
     * ��������˳�ʱʱ�����õ� 
     * 
     * 
     * @param conn
     * @throws InterruptedException
     */
    public void testDistributedLocking(Jedis conn)
            throws InterruptedException
    {
            System.out.println("\n----- testDistributedLocking -----");
            conn.del("lock:testlock");
            System.out.println("Getting an initial lock...");
            assert acquireLockWithTimeout(conn, "testlock", 1000, 1000) != null;
            System.out.println("Got it!");
            System.out.println("Trying to get it again without releasing the first one...");
            assert acquireLockWithTimeout(conn, "testlock", 10, 1000) == null;
            System.out.println("Failed to get it!");
            System.out.println();

            System.out.println("Waiting for the lock to timeout...");
            Thread.sleep(2000);
            System.out.println("Getting the lock again...");
            String lockId = acquireLockWithTimeout(conn, "testlock", 1000, 1000);
            assert lockId != null;
            System.out.println("Got it!");
            System.out.println("Releasing the lock...");
            assert releaseLock(conn, "testlock", lockId);
            System.out.println("Released it...");
            System.out.println();

            System.out.println("Acquiring it again...");
            assert acquireLockWithTimeout(conn, "testlock", 1000, 1000) != null;
            System.out.println("Got it!");
            conn.del("lock:testlock");
    }
    /**
     * ����˳�ʱʱ������õ���
     * 
     * page 125
     * �嵥 6-11
     * 
     * @param conn
     * @param lockName
     * @param acquireTimeout
     * @param lockTimeout
     * @return
     */
    public String acquireLockWithTimeout(
        Jedis conn, String lockName, long acquireTimeout, long lockTimeout)
    {
    	//128λ�����ʶ��
        String identifier = UUID.randomUUID().toString();
        String lockKey = "lock:" + lockName;
        
        //����ǿ��ת����ȷ������EXPIRE�Ķ�������
        int lockExpire = (int)(lockTimeout / 1000);

        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end) {
            if (conn.setnx(lockKey, identifier) == 1){
            	//��ȡ���������ù���ʱ��
                conn.expire(lockKey, lockExpire);
                return identifier;
            }
            //������ʱ�䣬������Ҫʱ������и���
            if (conn.ttl(lockKey) == -1) {
                conn.expire(lockKey, lockExpire);
            }

            try {
                Thread.sleep(1);
            }catch(InterruptedException ie){
                Thread.currentThread().interrupt();
            }
        }

        // null indicates that the lock was not acquired
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
 
    
}
