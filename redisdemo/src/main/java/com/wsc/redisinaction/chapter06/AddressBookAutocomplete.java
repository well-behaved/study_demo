package com.wsc.redisinaction.chapter06;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class AddressBookAutocomplete {

	public static void main(String[] args) 
		throws Exception
	{
	        new AddressBookAutocomplete().run();
	}
    public void run()
            throws InterruptedException, IOException
    {
            Jedis conn = new Jedis("localhost");
            conn.select(15);
            
            testAddressBookAutocomplete(conn);   
    }
    /**
     * ͨѶ¼�Զ���ȫ
     * 
     * 
     * @param conn
     */
    public void testAddressBookAutocomplete(Jedis conn) {
        System.out.println("\n----- testAddressBookAutocomplete -----");
        conn.del("members:test");
        System.out.println("the start/end range of 'abc' is: " +
            Arrays.toString(findPrefixRange("abc")));
        System.out.println();

        System.out.println("Let's add a few people to the guild");
        for (String name : new String[]{"jeff", "jenny", "jack", "jennifer"}){
            joinGuild(conn, "test", name);
        }
        System.out.println();
        System.out.println("now let's try to find users with names starting with 'je':");
        Set<String> r = autocompleteOnPrefix(conn, "test", "je");
        System.out.println(r);
        assert r.size() == 3;

        System.out.println("jeff just left to join a different guild...");
        leaveGuild(conn, "test", "jeff");
        r = autocompleteOnPrefix(conn, "test", "je");
        System.out.println(r);
        assert r.size() == 2;
        conn.del("members:test");
    }
    //׼��һ������֪�ַ���ɵ��б�
    private static final String VALID_CHARACTERS = "`abcdefghijklmnopqrstuvwxyz{";
    /**
     * ���ݸ���ǰ׺���ɲ��ҷ�Χ
     * 
     * page 113
     * �嵥6-3
     * 
     * @param prefix
     * @return
     */
    public String[] findPrefixRange(String prefix) {
        //���ַ��б��в���ǰ׺�ַ����ڵ�λ��
    	int posn = VALID_CHARACTERS.indexOf(prefix.charAt(prefix.length() - 1));
        //�ҵ�ǰ���ַ�
    	char suffix = VALID_CHARACTERS.charAt(posn > 0 ? posn - 1 : 0);
        
    	String start = prefix.substring(0, prefix.length() - 1) + suffix + '{';
        String end = prefix + '{';
        //���ط�Χ
        return new String[]{start, end};
    }
    
    /**
     * ���빫��
     * 
     * page 115
     * �嵥6-5
     * 
     * @param conn
     * @param guild
     * @param user
     */
    public void joinGuild(Jedis conn, String guild, String user) {
        conn.zadd("members:" + guild, 0, user);
    }

    /**
     * �뿪����
     * 
     * page 115
     * �嵵6-5
     * 
     * @param conn
     * @param guild
     * @param user
     */
    public void leaveGuild(Jedis conn, String guild, String user) {
        conn.zrem("members:" + guild, user);
    }

    /**
     * �Զ���ȫ������ͨ��ʹ��watch multi exec ȷ������ծ���в����ǲ��ᷢ���仯
     * 
     * page 114
     * �嵥6-4
     * 
     * @param conn
     * @param guild
     * @param prefix
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<String> autocompleteOnPrefix(Jedis conn, String guild, String prefix) {
    	//���ݸ�����ǰ׺��������ҷ�Χ�������յ�
        String[] range = findPrefixRange(prefix);
        String start = range[0];
        String end = range[1];
        String identifier = UUID.randomUUID().toString();
        start += identifier;
        end += identifier;
        String zsetName = "members:" + guild;

        //����Χ�����Ԫ�غͽ���Ԫ����ӵ����򼯺�����
        conn.zadd(zsetName, 0, start);
        conn.zadd(zsetName, 0, end);

        Set<String> items = null;
        while (true){
            conn.watch(zsetName);
            
            //�ҵ�����������Ԫ�������򼯺ϵ�����
            int sindex = conn.zrank(zsetName, start).intValue();
            int eindex = conn.zrank(zsetName, end).intValue();
            int erange = Math.min(sindex + 9, eindex - 2);

            //��������
            Transaction trans = conn.multi();
            
            //��ȡ��Χ�ڵ�ֵ��Ȼ��ɾ��֮ǰ�������ʼԪ�غͽ���Ԫ��
            trans.zrem(zsetName, start);
            trans.zrem(zsetName, end);
            trans.zrange(zsetName, sindex, erange);
            List<Object> results = trans.exec();
            if (results != null){
                items = (Set<String>)results.get(results.size() - 1);
                break;
            }
        }
        
        //����������������Զ���ȫ�Ĳ�������ִ�У�
        //��ô�ӻ�ȡ����Ԫ�������Ƴ���ʼԪ�غͽ���Ԫ��
        for (Iterator<String> iterator = items.iterator(); iterator.hasNext(); ){
            if (iterator.next().indexOf('{') != -1){
                iterator.remove();
            }
        }
        return items;
    }

}
