package com.xue.nettyserver.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/3/12 10:32
 * @Description:
 */
public class EchoClient {
    public static void main(String[] args) throws Exception {
        new EchoClient().start("localhost", 9090);
    }

    private void start(String host, int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                                 @Override
                                 protected void initChannel(SocketChannel ch) throws Exception {
                                     ch.pipeline().addLast(new EchoClientHandler());
                                 }
                             }
                    );
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        } finally{
            group.shutdownGracefully().sync();
        }
    }
}
