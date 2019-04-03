package com.xue.nettyserver.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.Random;

/**
 * 整数编码过滤器
 */
public class ToIntegerDecoder extends ByteToMessageDecoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in,
                       List<Object> out) throws Exception {
        if (in.readableBytes() >= 4) {
            /*
            检查是否至少有 4 字节可读(一个 int 的字节长度)
             */
            Integer value = in.readInt();
            System.out.println("value = "+value);
            out.add(value);
        }
        Thread.sleep(new Random().nextInt(300));
        System.out.println("do nothing");
    }
}

