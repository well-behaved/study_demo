package com.wsc.redisinaction.chapter01;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

import java.util.*;

public class Chapter01 {
	
	//׼������Ҫ�ĳ���
    private static final int ONE_WEEK_IN_SECONDS = 7 * 86400; //ÿ�ܵ�����
    private static final int VOTE_SCORE = 432;
    private static final int ARTICLES_PER_PAGE = 25;

    public static  void main(String[] args) {
        new Chapter01().run();
    }

    public void run() {
    	
    	//��������redis���ݿ���࣬Ĭ�ϱ��������ݿ�
        Jedis conn = new Jedis("localhost");
        	
        conn.select(15);
        
        //����������
        String articleId = postArticle(
            conn, "username", "A title", "http://www.google.com");
        System.out.println("  We posted a new article with id: " + articleId);
        System.out.println("Its HASH looks like:");
        Map<String,String> articleData = conn.hgetAll("article:" + articleId);
        for (Map.Entry<String,String> entry : articleData.entrySet()){
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        System.out.println();

        articleVote(conn, "other_user", "article:" + articleId);
        String votes = conn.hget("article:" + articleId, "votes");
        System.out.println(" We voted for the article, it now has votes: " + votes);
        assert Integer.parseInt(votes) > 1;

        System.out.println("The currently highest-scoring articles are:");
        List<Map<String,String>> articles = getArticles(conn, 1);
        printArticles(articles);
        assert articles.size() >= 1;
        
        addGroups(conn, articleId, new String[]{"new-group"});
        System.out.println("We added the article to a new group, other articles include:");
        articles = getGroupArticles(conn, "new-group", 1);
        printArticles(articles);
        assert articles.size() >= 1;
    }

    /**
     * ����������
     * 
     * page 18
     * �����嵥 1-7
     * 
     * @param conn
     * @param user
     * @param title
     * @param link
     * @return
     */
    public String postArticle(Jedis conn, String user, String title, String link) {
    	
    	//����һ���µ����µ�ID
        String articleId = String.valueOf(conn.incr("article:"));
        
        String voted = "voted:" + articleId;
        
        //�����������µ��û���ӵ����µ���ͶƱ�û�������
        conn.sadd(voted, user);
        
        //�������ͶƱ�������Ĺ���ʱ������Ϊһ��
        conn.expire(voted, ONE_WEEK_IN_SECONDS);

        //����javaʵ��һ��map����������Ϣ�洢�����map����
        long now = System.currentTimeMillis() / 1000;
        String article = "article:" + articleId;
        HashMap<String,String> articleData = new HashMap<String,String>();
        articleData.put("title", title);
        articleData.put("link", link);
        articleData.put("user", user);
        articleData.put("now", String.valueOf(now));
        articleData.put("votes", "1");
        
        //�������map�洢��һ��ɢ������
        conn.hmset(article, articleData);
        
        //��������ӵ����ݷ���ʱ����������򼯺�����
        conn.zadd("score:", now + VOTE_SCORE, article);
        
        //��������ӵ�����������������򼯺�
        conn.zadd("time:", now, article);

        return articleId;
    }

    
    /**
     * ͶƱ���ܵ�ʵ�ֺ���
     * 
     * page 17 
     * �����嵥1-6
     * 
     * @param conn
     * @param user
     * @param article
     */
    public void articleVote(Jedis conn, String user, String article) {
    	//��������ͶƱ�Ľ�ֹʱ��
        long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
        
        //��������Ƿ񻹿��Լ���ͶƱ
        if (conn.zscore("time:", article) < cutoff){
            return;
        }
        
        //��article:Id��ʶ����identifier������ȡ�����µ�id
        String articleId = article.substring(article.indexOf(':') + 1);
        
        //����û��ǵ�һ��Ϊ��ƪ����ͶƱ����ô������ƪ���µ�ͶƱ���ͷ���
        if (conn.sadd("voted:" + articleId, user) == 1) {
            conn.zincrby("score:", VOTE_SCORE, article);
            conn.hincrBy(article, "votes", 1l);
        }
    }

    //���溯���ĸ�������
    //����ģʽ��Ҳ��̫�����ʲôģʽ����̣�
    public List<Map<String,String>> getArticles(Jedis conn, int page) {
        return getArticles(conn, page, "score:");
    }

    
    /**
     * ��ȡ����
     * page 18 
     * 
     * �嵥 1-8
     * 
     * @param conn
     * @param page
     * @param order
     * @return ���ػ�ȡ����list
     */
    public List<Map<String,String>> getArticles(Jedis conn, int page, String order) {
    	
    	//��ȡ���µ���ʼ�����ͽ�������
        int start = (page - 1) * ARTICLES_PER_PAGE;
        int end = start + ARTICLES_PER_PAGE - 1;

        //��ȡ��������
        Set<String> ids = conn.zrevrange(order, start, end);
        
        //�������µ�ID��ȡ���µ���ϸ��Ϣ
        List<Map<String,String>> articles = new ArrayList<Map<String,String>>();
        for (String id : ids){
            Map<String,String> articleData = conn.hgetAll(id);
            articleData.put("id", id);
            articles.add(articleData);
        }

        return articles;
    }

    /**
     * ������ӵ�Ⱥ��
     * 
     * page 19
     * �嵥 1-9
     * 
     * @param conn
     * @param articleId
     * @param toAdd
     */
    public void addGroups(Jedis conn, String articleId, String[] toAdd) {
    	
    	//�����洢������Ϣ�ļ���
        String article = "article:" + articleId;
        
        //��������ӵ���������Ⱥ������
        for (String group : toAdd) {
            conn.sadd("group:" + group, article);
        }
    }

    //ͬ��
    public List<Map<String,String>> getGroupArticles(Jedis conn, String group, int page) {
        return getGroupArticles(conn, group, page, "score:");
    }

    /**
     * ��Ⱥ���л�ȡһ��ҳ����
     * 
     * page 20
     * �嵥1-10
     * 
     * @param conn
     * @param group
     * @param page
     * @param order
     * @return
     */
    public List<Map<String,String>> getGroupArticles(Jedis conn, String group, int page, String order) {
    	
    	//Ϊÿ��Ⱥ���ÿ�����򶼽���һ����
        String key = order + group;
        
        //����Ƿ��л����������
        //���û�еĻ������ڽ�������
        if (!conn.exists(key)) {
            //�������ֻ��߷���ʱ�䣬��Ⱥ�����½�������
        	ZParams params = new ZParams().aggregate(ZParams.Aggregate.MAX);
            conn.zinterstore(key, params, "group:" + group, order);
            
            //��redis��60��֮���Զ���ɾ���������ļ���
            conn.expire(key, 60);
        }
        
        //�ڷ��ص������У�����֮ǰ����ĺ��������з�ҳ����ȡ��������
        return getArticles(conn, page, key);
    }

    /**
     * ������������redisinaaction��û�У���Դ����ҵΪ�˷����ӡ�Լ����ϵ�
     * 
     * ��ӡ���������µ�������Ϣ
     * 
     * @param articles
     */
    private void printArticles(List<Map<String,String>> articles){
        for (Map<String,String> article : articles){
            System.out.println("  id: " + article.get("id"));
            for (Map.Entry<String,String> entry : article.entrySet()){
                if (entry.getKey().equals("id")){
                    continue;
                }
                System.out.println("    " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}

