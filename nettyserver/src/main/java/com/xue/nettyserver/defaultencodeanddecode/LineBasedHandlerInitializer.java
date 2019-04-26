package com.xue.nettyserver.defaultencodeanddecode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 *
 *处理由行尾符分隔的帧
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class LineBasedHandlerInitializer extends ChannelInitializer<Channel>
    {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LineBasedFrameDecoder(64 * 1024));
        //测试出入类
        pipeline.addLast(new FrameHandler());
    }

    public static final class FrameHandler
        extends SimpleChannelInboundHandler<ByteBuf> {
        /**
         * Is called for each message of type {@link I}.
         *
         * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
         *            belongs to
         * @param msg the message to handle
         * @throws Exception is thrown if an error occurred
         */
        @Override
        protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

        }
    }
}
