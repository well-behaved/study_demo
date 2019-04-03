package com.xue.nettyserver.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/4/3 10:15
 * @Description:
 */
public class ToIntegerDecoderTest {
    @Test
    public void testEncoded() {
        //设置传入数据
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 20; i++) {
            buf.writeInt(i);
        }
        //创建测试类
        EmbeddedChannel channel = new EmbeddedChannel(
                new ToIntegerDecoder());


        // 写入数据
        Assert.assertTrue(channel.writeInbound(buf));
        // 读取编码后数据
        for (int i = 1; i < 20; i++) {
            Assert.assertTrue(channel.readInbound().equals(i));
        }
    }
}
