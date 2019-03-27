package com.xue.nettyserver.bootstrap;

import io.netty.bootstrap.Bootstrap;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/3/26 18:06
 * @Description:
 */
public class BootstrapTest {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        // 设置用于处理 Channel 所有事件的 EventLoopGroup
        bootstrap.group(null);
        //channel()方法指定了Channel的实现类。如果该实现类 没提供默认的构造函数 1 ，可以通过调用channel- Factory()方法来指定一个工厂类，它将会被bind()方 法调用
        bootstrap.channel(null);
        //指定 Channel 应该绑定到的本地地址。如果没有指定， 则将由操作系统创建一个随机的地址。或者，也可以通过 bind()或者 connect()方法指定 localAddress
        bootstrap.localAddress(null);
        //设置 ChannelOption，其将被应用到每个新创建的 Channel 的 ChannelConfig。这些选项将会通过 bind()或者 connect()方法设置到 Channel，不管哪
        // 个先被调用。这个方法在 Channel 已经被创建后再调用 将不会有任何的效果。支持的 ChannelOption 取决于 使用的 Channel 类型。
        bootstrap.option(null, null);
        //指定新创建的 Channel 的属性值。这些属性值是通过 bind()或者 connect()方法设置到 Channel 的，具体 取决于谁最先被调用。这个方法在 Channel 被创建后将 不会有任何的效果。
        bootstrap.attr(null, null);
        //设置将被添加到 ChannelPipeline 以接收事件通知的handler
        bootstrap.handler(null);
        bootstrap.clone();
        //设置远程地址。或者，也可以通过 connect()方法来指 定它
        bootstrap.remoteAddress(null);
        // 连接到远程节点并返回一个 ChannelFuture，其将 会在 连接操作完成后接收到通知
        bootstrap.connect(null);
        // 绑定Channel并返回一个ChannelFuture，其将会在绑 定操作完成后接收到通知，在那之后必须调用 Channel.
        //         connect()方法来建立连接
        bootstrap.bind(null);
    }
}
