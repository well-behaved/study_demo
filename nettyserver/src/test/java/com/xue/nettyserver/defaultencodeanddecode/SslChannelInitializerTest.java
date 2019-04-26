//package com.xue.nettyserver.defaultencodeanddecode;
//
//import io.netty.buffer.ByteBufAllocator;
//import io.netty.handler.ssl.ApplicationProtocolNegotiator;
//import io.netty.handler.ssl.SslContext;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.asset.EmptyAsset;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import javax.net.ssl.SSLEngine;
//import javax.net.ssl.SSLSessionContext;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
///**
// * @Auther: xuexiong@souche.com
// * @Date: 2019/4/25 18:11
// * @Description:
// */
//public class SslChannelInitializerTest {
//    @Test
//    public void initChannel() {
//        SslContext sslContext = new SslContext() {
//            @Override
//            public boolean isClient() {
//                return false;
//            }
//
//            @Override
//            public List<String> cipherSuites() {
//                return null;
//            }
//
//            @Override
//            public long sessionCacheSize() {
//                return 0;
//            }
//
//            @Override
//            public long sessionTimeout() {
//                return 0;
//            }
//
//            @Override
//            public ApplicationProtocolNegotiator applicationProtocolNegotiator() {
//                return null;
//            }
//
//            @Override
//            public SSLEngine newEngine(ByteBufAllocator alloc) {
//                return null;
//            }
//
//            @Override
//            public SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
//                return null;
//            }
//
//            @Override
//            public SSLSessionContext sessionContext() {
//                return null;
//            }
//        };
//        SslChannelInitializer sslChannelInitializer =new SslChannelInitializer(sslContext,false);
//    }
//}
