package com.xue.nettyserver.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * 使用 option()方法来将 ChannelOption 应用到引导。你所提供的值将会被自动 应用到引导所创建的所有 Channel。可用的 ChannelOption 包括了底层连接的详细信息，如 keep-alive
 * 或者超时属性以及缓冲区设置。
 */
public class BootstrapClientWithOptionsAndAttrs {
    public static void main(String[] args) throws InterruptedException {
        bootstrap();
        Thread.sleep(3000*3);
    }

    /**
     * 使用option
     */
    public static void bootstrap() {
        final AttributeKey<Integer> id = AttributeKey.newInstance("ID");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(
                        new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                System.out.println("Received data");
                            }

                            @Override
                            public void channelRegistered(ChannelHandlerContext ctx)
                                    throws Exception {
                                //读取参数
                                Integer idValue = ctx.channel().attr(id).get();
                                System.out.println(idValue+"----idValue");
                                // do something with the idValue
                            }
                        }
                );
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        //设置参数
        bootstrap.attr(id, 123456);
        ChannelFuture future = bootstrap.connect(
                new InetSocketAddress("www.baidu.com", 80));
        future.syncUninterruptibly();
    }
}
