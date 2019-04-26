package com.xue.nettyserver.nettytest;

import com.xue.nettyserver.decode.WebSocketConvertHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/4/25 15:36
 * @Description:
 */
public class WebSocketConvertHandlerTest {
    @Test
    public void test(){
        /*
        输入值色红孩子
         */
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes("demo".getBytes());

        WebSocketConvertHandler.MyWebSocketFrame myWebSocketFrame
                = new WebSocketConvertHandler.MyWebSocketFrame(WebSocketConvertHandler.MyWebSocketFrame.FrameType.TEXT,byteBuf);

        EmbeddedChannel channel = new EmbeddedChannel(
                new WebSocketConvertHandler());


        channel.writeInbound(myWebSocketFrame);

        Object demo =  channel.readOutbound();
        channel.writeOutbound(myWebSocketFrame);

        Object demo2 =  channel.readInbound();
        System.out.println("over");


    }
}
