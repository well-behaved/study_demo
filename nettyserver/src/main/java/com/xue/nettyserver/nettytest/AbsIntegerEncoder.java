package com.xue.nettyserver.nettytest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 出站测试类
 * 编码器将从传入的 ByteBuf 中读取每个负整数，并将会调用 Math.abs()方法来获取
 * 其绝对值;
 * 编码器将会把每个负整数的绝对值写到 ChannelPipeline 中。
 */
public class AbsIntegerEncoder extends
    MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
        ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) {
            Integer inValue = in.readInt();
            System.out.println("传入值为："+inValue);
            int value = Math.abs(inValue);
            System.out.println("传出值为："+value);
            out.add(value);
        }
    }
}
