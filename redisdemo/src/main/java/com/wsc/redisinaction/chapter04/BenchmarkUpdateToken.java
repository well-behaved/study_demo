package com.wsc.redisinaction.chapter04;

import java.lang.reflect.Method;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class BenchmarkUpdateToken {
	public static final void main(String[] args) {
        new BenchmarkUpdateToken().run();
    }

    public void run() {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        testBenchmarkUpdateToken(conn);
    }

    public void testBenchmarkUpdateToken(Jedis conn) {
        System.out.println("\n----- testBenchmarkUpdate -----");
        benchmarkUpdateToken(conn, 5);
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
