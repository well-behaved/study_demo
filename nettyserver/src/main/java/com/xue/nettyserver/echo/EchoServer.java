package com.xue.nettyserver.echo;

import com.xue.nettyserver.common.CommonMethods;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/3/11 18:01
 * @Description: 服务端
 */
@ChannelHandler.Sharable
public class EchoServer {
    public static void main(String[] args) throws Exception {
        CommonMethods.sysImport("EchoServer begin");
        //设置端口值并且启动
        new EchoServer().start(9090);
//        while (true) {
//            Thread.sleep(1000000);
//        }
    }


    public void start(int port) throws Exception {
        //初始换自定义的业务处理handle
        final EchoServerHandler serverHandler = new EchoServerHandler();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    // 指定所使用的 NIO 传输 Channel
                    .channel(NioServerSocketChannel.class)
                    // 使用指定的 端口设置套 接字地址
                    .localAddress(new InetSocketAddress(port))
                    /*
                    ChannelInitializer  当一个新的连接 被接受时，一个新的子 Channel 将会被创建，而 ChannelInitializer 将会把一个你的
                    EchoServerHandler 的实例添加到该 Channel 的 ChannelPipeline 中。
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      protected void initChannel(SocketChannel ch) throws Exception {
                                          // 添加一个 EchoServer- Handler 到子 Channel 的 ChannelPipeline
                                          ch.pipeline().addLast(serverHandler);
                                      }
                                  }
                    );
            //异步地绑定服务器; 调用 sync()方法阻塞 等待直到绑定完成
            ChannelFuture f = b.bind().sync();
            CommonMethods.sysLog("绑定往完成");
            //获取 Channel 的 CloseFuture，并 且阻塞当前线 程直到它完成
            f.channel().closeFuture().sync();
        } finally {
            // 关闭 EventLoopGroup， 释放所有的资源
            group.shutdownGracefully().sync();
        }
    }
}
