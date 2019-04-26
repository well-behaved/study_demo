package com.xue.nettyserver.defaultencodeanddecode;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * Listing 11.6 Supporting WebSocket on the server
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class WebSocketServerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(
            new HttpServerCodec(),
            // http消息具体 为握手准备
            new HttpObjectAggregator(65536),
            // 对于请求/websocket 使用这个处理
            new WebSocketServerProtocolHandler("/websocket"),
            //文本类型处理
            new TextFrameHandler(),
            //二进制处理
            new BinaryFrameHandler(),
            // 数据帧:属于上一个 BinaryWebSocketFrame 或者 TextWeb- SocketFrame 的文本的或者二进制数据
            new ContinuationFrameHandler());
    }

    public static final class TextFrameHandler extends
            SimpleChannelInboundHandler<TextWebSocketFrame> {

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
            // Handle text frame
        }
    }

    public static final class BinaryFrameHandler extends
            SimpleChannelInboundHandler<BinaryWebSocketFrame> {
        @Override
        protected void messageReceived(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
            // Handle binary frame
        }
    }

    public static final class ContinuationFrameHandler extends
            SimpleChannelInboundHandler<ContinuationWebSocketFrame> {
        @Override
        protected void messageReceived(ChannelHandlerContext ctx, ContinuationWebSocketFrame msg) throws Exception {
            // Handle continuation frame
        }
    }
}
