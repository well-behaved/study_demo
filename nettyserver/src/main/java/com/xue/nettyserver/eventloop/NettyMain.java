package com.xue.nettyserver.eventloop;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/3/18 10:44
 * @Description:
 */
public class NettyMain {
    public static void main(String[] args) {
        // 例子 无法运行
        Channel ch = new NioServerSocketChannel();
        ScheduledFuture<?> future = ch.eventLoop().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("60 seconds later");
                    }
                }, 60, TimeUnit.SECONDS);
    }
}
