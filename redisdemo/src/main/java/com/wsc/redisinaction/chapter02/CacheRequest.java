package com.wsc.redisinaction.chapter02;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import redis.clients.jedis.Jedis;

public class CacheRequest {

	public static void main(String[] args) 
			throws InterruptedException
	{
		new CacheRequest().run();
	}
	
	public void run() 
			throws InterruptedException
	{	
		Jedis conn = new Jedis("localhost");
        conn.select(15);
        
        testCacheRequest(conn);
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
}
