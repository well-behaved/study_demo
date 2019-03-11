package com.xue.nettyserver.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;


/**
 * Created by RoyDeng on 17/7/20.
 */
public class HttpServer {

    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 9890;
        new HttpServer(port).start();

        //主线程休眠等待 此时可以请求 localhost9890
        Thread.sleep(600000);
        System.out.println("main over");
    }

    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        System.out.println("initChannel ch:" + ch);
                        ch.pipeline()
                                //用于解码request
                                .addLast("decoder", new HttpRequestDecoder())
                                // 用于编码response
                                .addLast("encoder", new HttpResponseEncoder())
                                /*
                                消息聚合器（重要）
                                为什么能有FullHttpRequest这个东西，就是因为有他，HttpObjectAggregator，
                                如果没有他，就不会有那个消息是FullHttpRequest的那段Channel，同样也不会有FullHttpResponse
                                消息合并的数据大小，如此代表聚合的消息内容长度不超过512kb。
                                 */
                                .addLast("aggregator", new HttpObjectAggregator(512 * 1024))    // 3
                                //添加我们自己的处理接口
                                .addLast("handler", new HttpHandler());        // 4
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        b.bind(port).sync();
        System.out.println("start over");
    }
}