package com.xue.nettyserver.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;

/**
 * 引导使你的应用程序启动并且运行起来，但是迟早你都需要优雅地将它关闭。当然，你也可 以让 JVM 在退出时处理好一切，但是这不符合优雅的定义，优雅是指干净地释放资源。关闭 Netty
 * 应用程序并没有太多的魔法，但是还是有些事情需要记在心上。
 * 最重要的是，你需要关闭 EventLoopGroup，它将处理任何挂起的事件和任务，并且随后 释放所有活动的线程。这就是调用 EventLoopGroup.shutdownGracefully()方法的作用。
 * 这个方法调用将会返回一个 Future，这个 Future 将在关闭完成时接收到通知。需要注意的是， shutdownGracefully()方法也是一个异步的操作，所以你需要阻塞等待直到它完成，或者向 所返回的 Future
 * 注册一个监听器以在关闭完成时获得通知。
 */
public class GracefulShutdown {
    public static void main(String args[]) {
        GracefulShutdown client = new GracefulShutdown();
        client.bootstrap();
    }

    public void bootstrap() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                //...
                .handler(
                        new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                System.out.println("Received data");
                            }
                        }
                );
        bootstrap.connect(new InetSocketAddress("www.baidu.com", 80)).syncUninterruptibly();
        //将释放 所有的资源，并且关闭所有的当 前正在使用中的 Channel
        Future<?> future = group.shutdownGracefully();
        future.syncUninterruptibly();
    }
}
