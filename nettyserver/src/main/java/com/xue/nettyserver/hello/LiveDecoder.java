package com.xue.nettyserver.hello;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 继承ReplayingDecoder，泛型LiveState，用来表示当前读取的状态
 * 描述LiveState，有读取长度和读取内容两个状态
 * 初始化的时候设置为读取长度的状态
 * 读取的时候通过state()方法来确定当前处于什么状态
 * 如果读取出来的长度大于0，则设置为读取内容状态，下一次读取的时候则从这个位置开始
 * <p>
 * 读取完成，往结果里面放解析好的数据
 */
public class LiveDecoder extends ReplayingDecoder<LiveDecoder.LiveState> { //1

    public enum LiveState { //2
        LENGTH,
        CONTENT
    }

    private LiveMessage message = new LiveMessage();

    public LiveDecoder() {
        //初始化的时候设置为读取长度的状态
        super(LiveState.LENGTH);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws
            Exception {
        //读取的时候通过state()方法来确定当前处于什么状态
        switch (state()) {
            case LENGTH:
                int length = byteBuf.readInt();
                if (length > 0) {
                    //如果读取出来的长度大于0，则设置为读取内容状态，下一次读取的时候则从这个位置开始
                    checkpoint(LiveState.CONTENT);
                } else {
                    //读取完成，往结果里面放解析好的数据
                    list.add(message);
                }
                break;
            case CONTENT:
                byte[] bytes = new byte[message.getLength()];
                byteBuf.readBytes(bytes);
                String content = new String(bytes);
                message.setContent(content);
                list.add(message);
                break;
            default:
                throw new IllegalStateException("invalid state:" + state());
        }
    }
}