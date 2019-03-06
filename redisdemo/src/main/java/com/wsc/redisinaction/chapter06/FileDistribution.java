package com.wsc.redisinaction.chapter06;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

public class FileDistribution {
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

            testFileDistribution(conn);
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
