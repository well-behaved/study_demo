package com.xue.nettyserver.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * netty的一般准则
 * 共用一个evenloop测试
 *
 *
 * 通过将已被接受的子 Channel 的 EventLoop 传递给 Bootstrap 的 group()方法来共享该 EventLoop。因为分配给 EventLoop 的所有 Channel 都使用同一
 * 个线程，所以这避免了额外的线程创建，以及前面所提到的相关的上下文切换。
 */
public class BootstrapSharingEventLoopGroup {

    public void bootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                //绑定
                .channel(NioServerSocketChannel.class)
                //绑定一个hander
                .childHandler(
                        new SimpleChannelInboundHandler<ByteBuf>() {
                            ChannelFuture connectFuture;

                            @Override
                            protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                if (connectFuture.isDone()) {
                                    System.out.println("nothing");
                                }
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx)
                                    throws Exception {
                                /*
                                 *在这里创建另一个子连接，共用一个eventLoop
                                 */
                                Bootstrap bootstrap = new Bootstrap();
                                bootstrap.channel(NioSocketChannel.class).handler(
                                        new SimpleChannelInboundHandler<ByteBuf>() {
                                            @Override
                                            protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg)
                                                    throws
                                                    Exception {
                                                System.out.println("Received data");
                                            }
                                        });
                                //共用一个eventloop
                                bootstrap.group(ctx.channel().eventLoop());
                                connectFuture = bootstrap.connect(
                                        new InetSocketAddress("www.manning.com", 80));
                            }
                        });
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture)
                    throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("Server bound");
                } else {
                    System.err.println("Bind attempt failed");
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }
}
