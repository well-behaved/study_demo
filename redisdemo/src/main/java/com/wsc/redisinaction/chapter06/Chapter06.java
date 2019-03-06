package com.wsc.redisinaction.chapter06;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Chapter06 {
    public static final void main(String[] args)
        throws Exception
    {
        new Chapter06().run();
    }

    public void run()
        throws InterruptedException, IOException
    {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        testAddUpdateContact(conn);
        testAddressBookAutocomplete(conn);
        testDistributedLocking(conn);
        testCountingSemaphore(conn);
        testDelayedTasks(conn);
        testMultiRecipientMessaging(conn);
        testFileDistribution(conn);
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

    public void testMultiRecipientMessaging(Jedis conn) {
        System.out.println("\n----- testMultiRecipientMessaging -----");
        conn.del("ids:chat:", "msgs:1", "ids:1", "seen:joe", "seen:jeff", "seen:jenny");

        System.out.println("Let's create a new chat session with some recipients...");
        Set<String> recipients = new HashSet<String>();
        recipients.add("jeff");
        recipients.add("jenny");
        String chatId = createChat(conn, "joe", recipients, "message 1");
        System.out.println("Now let's send a few messages...");
        for (int i = 2; i < 5; i++){
            sendMessage(conn, chatId, "joe", "message " + i);
        }
        System.out.println();

        System.out.println("And let's get the messages that are waiting for jeff and jenny...");
        List<ChatMessages> r1 = fetchPendingMessages(conn, "jeff");
        List<ChatMessages> r2 = fetchPendingMessages(conn, "jenny");
        System.out.println("They are the same? " + r1.equals(r2));
        assert r1.equals(r2);
        System.out.println("Those messages are:");
        for(ChatMessages chat : r1){
            System.out.println("  chatId: " + chat.chatId);
            System.out.println("    messages:");
            for(Map<String,Object> message : chat.messages){
                System.out.println("      " + message);
            }
        }

        conn.del("ids:chat:", "msgs:1", "ids:1", "seen:joe", "seen:jeff", "seen:jenny");
    }

    public void testFileDistribution(Jedis conn)
        throws InterruptedException, IOException
    {
        System.out.println("\n----- testFileDistribution -----");
        String[] keys = conn.keys("test:*").toArray(new String[0]);
        if (keys.length > 0){
            conn.del(keys);
        }
        conn.del(
            "msgs:test:",
            "seen:0",
            "seen:source",
            "ids:test:",
            "chat:test:");

        System.out.println("Creating some temporary 'log' files...");
        File f1 = File.createTempFile("temp_redis_1_", ".txt");
        f1.deleteOnExit();
        Writer writer = new FileWriter(f1);
        writer.write("one line\n");
        writer.close();

        File f2 = File.createTempFile("temp_redis_2_", ".txt");
        f2.deleteOnExit();
        writer = new FileWriter(f2);
        for (int i = 0; i < 100; i++){
            writer.write("many lines " + i + '\n');
        }
        writer.close();

        File f3 = File.createTempFile("temp_redis_3_", ".txt.gz");
        f3.deleteOnExit();
        writer = new OutputStreamWriter(
            new GZIPOutputStream(
                new FileOutputStream(f3)));
        Random random = new Random();
        for (int i = 0; i < 1000; i++){
            writer.write("random line " + Long.toHexString(random.nextLong()) + '\n');
        }
        writer.close();

        long size = f3.length();
        System.out.println("Done.");
        System.out.println();
        System.out.println("Starting up a thread to copy logs to redis...");
        File path = f1.getParentFile();
        CopyLogsThread thread = new CopyLogsThread(path, "test:", 1, size);
        thread.start();

        System.out.println("Let's pause to let some logs get copied to Redis...");
        Thread.sleep(250);
        System.out.println();
        System.out.println("Okay, the logs should be ready. Let's process them!");

        System.out.println("Files should have 1, 100, and 1000 lines");
        TestCallback callback = new TestCallback();
        processLogsFromRedis(conn, "0", callback);
        System.out.println(Arrays.toString(callback.counts.toArray(new Integer[0])));
        assert callback.counts.get(0) == 1;
        assert callback.counts.get(1) == 100;
        assert callback.counts.get(2) == 1000;

        System.out.println();
        System.out.println("Let's wait for the copy thread to finish cleaning up...");
        thread.join();
        System.out.println("Done cleaning out Redis!");

        keys = conn.keys("test:*").toArray(new String[0]);
        if (keys.length > 0){
            conn.del(keys);
        }
        conn.del(
            "msgs:test:",
            "seen:0",
            "seen:source",
            "ids:test:",
            "chat:test:");
    }

    public class TestCallback
        implements Callback
    {
        private int index;
        public List<Integer> counts = new ArrayList<Integer>();

        public void callback(String line){
            if (line == null){
                index++;
                return;
            }
            while (counts.size() == index){
                counts.add(0);
            }
            counts.set(index, counts.get(index) + 1);
        }
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

    /**
     * ����һ���µ�Ⱥ��IDȻ�󴫸���һ������
     * 
     * @param conn
     * @param sender
     * @param recipients
     * @param message
     * @return
     */
    public String createChat(Jedis conn, String sender, Set<String> recipients, String message) {
    	//��ȡ�µ�Ⱥ��ID
        String chatId = String.valueOf(conn.incr("ids:chat:"));
        return createChat(conn, sender, recipients, message, chatId);
    }

    /**
     * 
     * ����Ⱥ������
     * 
     * page 142 
     * �嵥 6-24
     * 
     * @param conn
     * @param sender
     * @param recipients
     * @param message
     * @param chatId
     * @return
     */
    public String createChat(
        Jedis conn, String sender, Set<String> recipients, String message, String chatId)
    {
    	//����һ�����û��ͷ�����ɵ��ֵ䣬�ֵ��������Ϣ������ӵ����򼯺�����
        recipients.add(sender);

        Transaction trans = conn.multi();
        for (String recipient : recipients){
        	//�����в���Ⱥ�ĵ��û���ӵ����򼯺�����
            trans.zadd("chat:" + chatId, 0, recipient);
            
            //��ʼ���Ѷ����򼯺�
            trans.zadd("seen:" + recipient, 0, chatId);
        }
        //ִ������
        trans.exec();

        //������Ϣ������sendMessage����
        return sendMessage(conn, chatId, sender, message);
    }

    /**
     * ������Ϣ�� ʹ������ʵ�ֵ���Ϣ���Ͳ���
     * 
     *page 143
     *�嵥6-25
     * 
     * @param conn
     * @param chatId
     * @param sender
     * @param message
     * @return
     */
    public String sendMessage(Jedis conn, String chatId, String sender, String message) {
    	
    	//��ȡ��
        String identifier = acquireLock(conn, "chat:" + chatId);
        if (identifier == null){
            throw new RuntimeException("Couldn't get the lock");
        }
        try {
        	//�ﱸ�����͵ģ�
            long messageId = conn.incr("ids:" + chatId);
            
            //�����͵���Ϣ�ȴ��뵽hashmap��
            HashMap<String,Object> values = new HashMap<String,Object>();
            values.put("id", messageId);
            values.put("ts", System.currentTimeMillis());
            values.put("sender", sender);
            values.put("message", message);
            
            //Ȼ��hashmapתΪjson��ʽ���ַ���
            String packed = new Gson().toJson(values);
            
            //����Ϣ������Ⱥ��
            conn.zadd("msgs:" + chatId, messageId, packed);
        }finally{
        	//����ͷ���
            releaseLock(conn, "chat:" + chatId, identifier);
        }
        return chatId;
    }

    /**
     * ����������������1������������û������
     * 
     * page 144
     * 
     * 
     * @param conn
     * @param recipient
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<ChatMessages> fetchPendingMessages(Jedis conn, String recipient) {
    	
    	//��ȡ�����յ���Ϣ��ID
        Set<Tuple> seenSet = conn.zrangeWithScores("seen:" + recipient, 0, -1);
        List<Tuple> seenList = new ArrayList<Tuple>(seenSet);

        //��ȡ����δ����Ϣ
        Transaction trans = conn.multi();
        for (Tuple tuple : seenList){
            String chatId = tuple.getElement();
            int seenId = (int)tuple.getScore();
            trans.zrangeByScore("msgs:" + chatId, String.valueOf(seenId + 1), "inf");
        }
        List<Object> results = trans.exec();

        Gson gson = new Gson();
        Iterator<Tuple> seenIterator = seenList.iterator();
        Iterator<Object> resultsIterator = results.iterator();

        List<ChatMessages> chatMessages = new ArrayList<ChatMessages>();
        List<Object[]> seenUpdates = new ArrayList<Object[]>();
        List<Object[]> msgRemoves = new ArrayList<Object[]>();
        while (seenIterator.hasNext()){
            Tuple seen = seenIterator.next();
            Set<String> messageStrings = (Set<String>)resultsIterator.next();
            if (messageStrings.size() == 0){
                continue;
            }

            int seenId = 0;
            String chatId = seen.getElement();
            List<Map<String,Object>> messages = new ArrayList<Map<String,Object>>();
            for (String messageJson : messageStrings){
                Map<String,Object> message = (Map<String,Object>)gson.fromJson(
                    messageJson, new TypeToken<Map<String,Object>>(){}.getType());
                int messageId = ((Double)message.get("id")).intValue();
                if (messageId > seenId){
                    seenId = messageId;
                }
                message.put("id", messageId);
                messages.add(message);
            }

            conn.zadd("chat:" + chatId, seenId, recipient);
            seenUpdates.add(new Object[]{"seen:" + recipient, seenId, chatId});

            Set<Tuple> minIdSet = conn.zrangeWithScores("chat:" + chatId, 0, 0);
            if (minIdSet.size() > 0){
                msgRemoves.add(new Object[]{
                    "msgs:" + chatId, minIdSet.iterator().next().getScore()});
            }
            chatMessages.add(new ChatMessages(chatId, messages));
        }

        trans = conn.multi();
        for (Object[] seenUpdate : seenUpdates){
            trans.zadd(
                (String)seenUpdate[0],
                (Integer)seenUpdate[1],
                (String)seenUpdate[2]);
        }
        for (Object[] msgRemove : msgRemoves){
            trans.zremrangeByScore(
                (String)msgRemove[0], 0, ((Double)msgRemove[1]).intValue());
        }
        trans.exec();

        return chatMessages;
    }

    /**
     * ������־�ļ� 
     * 
     * page 149
     * 
     * 
     * @param conn
     * @param id
     * @param callback
     * @throws InterruptedException
     * @throws IOException
     */
    public void processLogsFromRedis(Jedis conn, String id, Callback callback)
        throws InterruptedException, IOException
    {
        while (true){
        	//��ȡ�ļ��б�
            List<ChatMessages> fdata = fetchPendingMessages(conn, id);

            for (ChatMessages messages : fdata){
                for (Map<String,Object> message : messages.messages){
                    String logFile = (String)message.get("message");
                    
                    //������־���Ѿ��������
                    if (":done".equals(logFile)){
                        return;
                    }
                    if (logFile == null || logFile.length() == 0){
                        continue;
                    }

                    //ѡ��һ�����ȡ��
                    InputStream in = new RedisInputStream(
                        conn, messages.chatId + logFile);
                    if (logFile.endsWith(".gz")){
                        in = new GZIPInputStream(in);
                    }

                    //������־��
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    try{
                        String line = null;
                        while ((line = reader.readLine()) != null){
                            //����־�д��ݸ��ص�����
                        	callback.callback(line);
                        }
                        //ǿ��ˢ�¾ۺ����ݻ���
                        callback.callback(null);
                    }finally{
                        reader.close();
                    }

                    //��־�Ѿ�������ϣ����ļ������߱�����һ��Ϣ
                    conn.incr(messages.chatId + logFile + ":done");
                }
            }

            if (fdata.size() == 0){
                Thread.sleep(100);
            }
        }
    }

    /**
     * ���ȡ������rides�ڴ��ж�ȡ��Ϣ
     * 
     * page 150�Ժ�
     * 
     * @author wsc
     *
     */
    public class RedisInputStream
        extends InputStream
    {
        private Jedis conn;
        private String key;
        private int pos;

        public RedisInputStream(Jedis conn, String key){
            this.conn = conn;
            this.key = key;
        }

        @Override
        public int available()
            throws IOException
        {
            long len = conn.strlen(key);
            return (int)(len - pos);
        }

        @Override
        public int read()
            throws IOException
        {
            byte[] block = conn.substr(key.getBytes(), pos, pos);
            if (block == null || block.length == 0){
                return -1;
            }
            pos++;
            return (int)(block[0] & 0xff);
        }

        @Override
        public int read(byte[] buf, int off, int len)
            throws IOException
        {
            byte[] block = conn.substr(key.getBytes(), pos, pos + (len - off - 1));
            if (block == null || block.length == 0){
                return -1;
            }
            System.arraycopy(block, 0, buf, off, block.length);
            pos += block.length;
            return block.length;
        }

        @Override
        public void close() {
            // no-op
        }
    }

    public interface Callback {
        void callback(String line);
    }

    /**
     * �ڲ��ඨ��һЩ����ģ�������
     * 
     * @author wsc
     *
     */
    public class ChatMessages
    {
        public String chatId;
        public List<Map<String,Object>> messages;

        public ChatMessages(String chatId, List<Map<String,Object>> messages){
            this.chatId = chatId;
            this.messages = messages;
        }

        public boolean equals(Object other){
            if (!(other instanceof ChatMessages)){
                return false;
            }
            ChatMessages otherCm = (ChatMessages)other;
            return chatId.equals(otherCm.chatId) &&
                messages.equals(otherCm.messages);
        }
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

    /**
     * ������־�ļ�������־�ļ�����redis��
     * 
     * 
     * @author wsc
     *
     */
    public class CopyLogsThread
        extends Thread
    {
        private Jedis conn;
        private File path;
        private String channel;
        private int count;
        private long limit;

        public CopyLogsThread(File path, String channel, int count, long limit) {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
            this.path = path;
            this.channel = channel;
            this.count = count;
            this.limit = limit;
        }

        public void run() {
            Deque<File> waiting = new ArrayDeque<File>();
            long bytesInRedis = 0;

            Set<String> recipients= new HashSet<String>();
            for (int i = 0; i < count; i++){
                recipients.add(String.valueOf(i));
            }
            
            //����������ͻ��˷�����Ϣ��Ⱥ��
            createChat(conn, "source", recipients, "", channel);
            File[] logFiles = path.listFiles(new FilenameFilter(){
                public boolean accept(File dir, String name){
                    return name.startsWith("temp_redis");
                }
            });
            Arrays.sort(logFiles);
            //�������е���־�ļ�
            for (File logFile : logFiles){
                long fsize = logFile.length();
                
                //���������Ҫ����Ŀؼ�����ô�����Ѿ�������ϵ��ļ�
                while ((bytesInRedis + fsize) > limit){
                    long cleaned = clean(waiting, count);
                    if (cleaned != 0){
                        bytesInRedis -= cleaned;
                    }else{
                        try{
                            sleep(250);
                        }catch(InterruptedException ie){
                            Thread.interrupted();
                        }
                    }
                }

                BufferedInputStream in = null;
                try{
                    in = new BufferedInputStream(new FileInputStream(logFile));
                    int read = 0;
                    byte[] buffer = new byte[8192];
                    
                    //���ļ��ϴ���redis
                    while ((read = in.read(buffer, 0, buffer.length)) != -1){
                        if (buffer.length != read){
                            byte[] bytes = new byte[read];
                            System.arraycopy(buffer, 0, bytes, 0, read);
                            conn.append((channel + logFile).getBytes(), bytes);
                        }else{
                            conn.append((channel + logFile).getBytes(), buffer);
                        }
                    }
                }catch(IOException ioe){
                    ioe.printStackTrace();
                    throw new RuntimeException(ioe);
                }finally{
                    try{
                        in.close();
                    }catch(Exception ignore){
                    }
                }
                //���Ѽ����ţ��ļ��Ѿ�׼������
                sendMessage(conn, channel, "source", logFile.toString());

                //�Ա��ؼ�¼��redis�ڴ�ռ������ص���Ϣ���и���
                bytesInRedis += fsize;
                waiting.addLast(logFile);
            }
            
            //������־�ļ��Ѿ�������ϣ�������߱������
            sendMessage(conn, channel, "source", ":done");

            //�ڹ������֮���������õ���־�ļ�
            while (waiting.size() > 0){
                long cleaned = clean(waiting, count);
                if (cleaned != 0){
                    bytesInRedis -= cleaned;
                }else{
                    try{
                        sleep(250);
                    }catch(InterruptedException ie){
                        Thread.interrupted();
                    }
                }
            }
        }
        
        //��redis�����������ϸ����
        private long clean(Deque<File> waiting, int count) {
            if (waiting.size() == 0){
                return 0;
            }
            File w0 = waiting.getFirst();
            if (String.valueOf(count).equals(conn.get(channel + w0 + ":done"))){
                conn.del(channel + w0, channel + w0 + ":done");
                return waiting.removeFirst().length();
            }
            return 0;
        }
    }
}
