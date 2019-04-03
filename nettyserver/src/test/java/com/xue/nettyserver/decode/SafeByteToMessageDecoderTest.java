package com.xue.nettyserver.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/4/3 17:07
 * @Description:
 */
public class SafeByteToMessageDecoderTest {

    @Test
    public void decode() {
        //设置传入数据
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 5000000; i++) {
            buf.writeInt(i);
        }
        //创建测试类
        EmbeddedChannel channel = new EmbeddedChannel(new SafeByteToMessageDecoder());
        // 写入数据
       channel.writeInbound(buf);
        // 读取编码后数据
        for (int i = 1; i < 2; i++) {
            Object object = channel.readInbound();
            System.out.println(object);
//            Assert.assertTrue(object.equals(i));
        }
    }
}
