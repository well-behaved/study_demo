package com.xue.nettyserver.defaultencodeanddecode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 如果连接空闲时间过长，的发送一个心跳信息 测试 发送失败则变比连接
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel>
    {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(
                //添加一个 连接空闲时间太长时，将会触发一个 IdleStateEvent 事件 的handler
                new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
        //添加一个心跳服务handler
        pipeline.addLast(new HeartbeatHandler());
    }

    public static final class HeartbeatHandler
        extends ChannelInboundHandlerAdapter {
        //心跳包准备
        private static final ByteBuf HEARTBEAT_SEQUENCE =
                Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(
                "HEARTBEAT", CharsetUtil.ISO_8859_1));
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx,
            Object evt) throws Exception {
            // ChannelInboundHandler 中重写 userEvent- Triggered()方法来处理该 IdleStateEvent 事件
            if (evt instanceof IdleStateEvent) {
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                        //如果发送失败 则关闭连接
                     .addListener(
                         ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                //向下传递
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}
