package com.xue.nettyserver.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.oio.OioDatagramChannel;

import java.net.InetSocketAddress;

/**
 * 无连接的协议 udp
 * 不再调用 connect()方法，而是只调用 bind()方法，
 */
public class BootstrapDatagramChannel {

    public void bootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new OioEventLoopGroup()).channel(
            OioDatagramChannel.class).handler(
            new SimpleChannelInboundHandler<DatagramPacket>() {
                @Override
                protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                    System.out.println("do sometiong");
                }

            }
        );
        //绑定
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(0));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture)
               throws Exception {
               if (channelFuture.isSuccess()) {
                   System.out.println("Channel bound");
               } else {
                   System.err.println("Bind attempt failed");
                   channelFuture.cause().printStackTrace();
               }
            }
        });
    }
}
