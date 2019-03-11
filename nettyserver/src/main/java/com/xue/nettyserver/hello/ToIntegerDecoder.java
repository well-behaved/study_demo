package com.xue.nettyserver.hello;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ToIntegerDecoder extends ByteToMessageDecoder {  //1

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
        /*
        从这段代码可以看出，因为不知道这次请求发过来多少数据，所以每次都要判断byte长度够不够4，如果你的数据长度更长，
        且不固定的话，这里的逻辑会变得非常复杂。所以在这里介绍另一个我们常用的解码器
        ：ReplayingDecoder。
         */
        if (in.readableBytes() >= 4) {  //2
            out.add(in.readInt());  //3
        }
    }
}