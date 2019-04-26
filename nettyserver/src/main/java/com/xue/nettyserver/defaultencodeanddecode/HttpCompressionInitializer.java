package com.xue.nettyserver.defaultencodeanddecode;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 自动压缩 HTTP 消息
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class HttpCompressionInitializer extends ChannelInitializer<Channel> {
    private final boolean isClient;

    public HttpCompressionInitializer(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (isClient) {
            //添加http
            pipeline.addLast("codec", new HttpClientCodec());
            // 添加解压缩
            pipeline.addLast("decompressor",
            new HttpContentDecompressor());
        } else {
            //添加http
            pipeline.addLast("codec", new HttpServerCodec());
            // 添加压缩
            pipeline.addLast("compressor",
            new HttpContentCompressor());
        }
    }
}
