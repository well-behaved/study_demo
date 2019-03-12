package com.xue.nettyserver.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/3/11 17:53
 * @Description: 服务端业务逻辑
 *
 *
 *
 * channelRead()— 对于每个传入的消息都要调用;
 * channelReadComplete()— 通知ChannelInboundHandler最后一次对channel-Read()的调用是当前批量读取中的最后一条消息;
 * exceptionCaught()— 在读取操作期间，有异常抛出时会调用。
 * <p>
 * 如果不捕获异常，会发生什么呢
 * 每个 Channel 都拥有一个与之相关联的 ChannelPipeline，其持有一个 ChannelHandler 的 实例链。在默认的情况下，ChannelHandler 会把对它的方法的调用转发给链中的下一个
 * Channel- Handler。因此，如果 exceptionCaught()方法没有被该链中的某处实现，那么所接收的异常将会被 传递到 ChannelPipeline
 * 的尾端并被记录。为此，你的应用程序应该提供至少有一个实现了 exceptionCaught()方法的 ChannelHandler。
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        // 将消息 记录到 控制台
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
        //将接收到的消息 写给发送者，而 不冲刷出站消息
        ctx.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // 将未决消息冲刷到 远程节点，并且关 闭该 Channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        //异常打印
        cause.printStackTrace();
        ctx.close();
    }
}
