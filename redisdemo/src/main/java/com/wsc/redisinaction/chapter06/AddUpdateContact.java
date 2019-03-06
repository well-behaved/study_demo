package com.wsc.redisinaction.chapter06;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class AddUpdateContact {
    public static final void main(String[] args)
    	throws Exception
    {
        new AddUpdateContact().run();
    }
    public void run()
        throws InterruptedException, IOException
    {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        testAddUpdateContact(conn);
        
    }
    public void testAddUpdateContact(Jedis conn) {
        System.out.println("\n----- testAddUpdateContact -----");
        conn.del("recent:user");

        System.out.println("Let's add a few contacts...");
        for (int i = 0; i < 10; i++){
            addUpdateContact(conn, "user", "contact-" + ((int)Math.floor(i / 3)) + '-' + i);
        }
        System.out.println("Current recently contacted contacts");
        List<String> contacts = conn.lrange("recent:user", 0, -1);
        for(String contact : contacts){
            System.out.println("  " + contact);
        }
        assert contacts.size() >= 10;
        System.out.println();

        System.out.println("Let's pull one of the older ones up to the front");
        addUpdateContact(conn, "user", "contact-1-4");
        contacts = conn.lrange("recent:user", 0, 2);
        System.out.println("New top-3 contacts:");
        for(String contact : contacts){
            System.out.println("  " + contact);
        }
        assert "contact-1-4".equals(contacts.get(0));
        System.out.println();

        System.out.println("Let's remove a contact...");
        removeContact(conn, "user", "contact-2-6");
        contacts = conn.lrange("recent:user", 0, -1);
        System.out.println("New contacts:");
        for(String contact : contacts){
            System.out.println("  " + contact);
        }
        assert contacts.size() >= 9;
        System.out.println();

        System.out.println("And let's finally autocomplete on ");
        List<String> all = conn.lrange("recent:user", 0, -1);
        contacts = fetchAutocompleteList(conn, "user", "c");
        assert all.equals(contacts);
        List<String> equiv = new ArrayList<String>();
        for (String contact : all){
            if (contact.startsWith("contact-2-")){
                equiv.add(contact);
            }
        }
        contacts = fetchAutocompleteList(conn, "user", "contact-2-");
        Collections.sort(equiv);
        Collections.sort(contacts);
        assert equiv.equals(contacts);
        conn.del("recent:user");
    }

    /**
     * ����µ��û����������µ��б�
     * 
     * page 111
     * �����嵥 6-1
     * 
     * @param conn ���ݿ�����
     * @param user �û�
     * @param contact Ҫ��ӵ��û�
     */
    public void addUpdateContact(Jedis conn, String user, String contact) {
        String acList = "recent:" + user;
        
        //��������׼��ִ��ԭ�Ӳ���
        Transaction trans = conn.multi();
        //�����ϵ���Ѿ����ڣ���ô�Ƴ���
        trans.lrem(acList, 0, contact);
        //����ϵ�������б����ǰ��
        trans.lpush(acList, contact);
        //ֻ�����б������ǰ100����ϵ��
        trans.ltrim(acList, 0, 99);
        //��������ִ������Ĳ���
        trans.exec();
    }

    /**
     * ����ϵ�˴��û����б���ɾ��
     * 
     * page 111
     * 
     * 
     * @param conn	���ݿ�����
     * @param user	�û���
     * @param contact	��ϵ��
     */
    public void removeContact(Jedis conn, String user, String contact) {
        conn.lrem("recent:" + user, 0, contact);
    }

    /**
     * ��ȡ�Զ���ȫ�б�����ƥ����û�
     * 
     * page 111
     * �嵥6-2
     * 
     * @param conn
     * @param user
     * @param prefix
     * @return
     */
    public List<String> fetchAutocompleteList(Jedis conn, String user, String prefix) {
    	//��ȡ�Զ���ȫ���б�
        List<String> candidates = conn.lrange("recent:" + user, 0, -1);
        List<String> matches = new ArrayList<String>();
        //���ÿ����ѡ��ϵ��
        for (String candidate : candidates) {
            if (candidate.toLowerCase().startsWith(prefix)){
                //����һ��ƥ�����ϵ��
            	matches.add(candidate);
            }
        }
        //��������ƥ�����ϵ��
        return matches;
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
