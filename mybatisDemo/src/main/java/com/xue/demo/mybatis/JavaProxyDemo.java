package com.xue.demo.mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * java 代理类demo
 */
public class JavaProxyDemo {
    public static void main(String[] args) {
        //生成代理类
        MoviePlay moviePlayProxyImpl = (MoviePlay) Proxy.newProxyInstance(MoviePlay.class.getClassLoader(),
                new Class[]{MoviePlay.class}, new MoviePlayProxyImpl());

        moviePlayProxyImpl.play();
    }
}

/**
 * 放电影接口
 */
interface MoviePlay {
    void play();
}

/**
 * 放电影代理类
 */
class MoviePlayProxyImpl implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("电影开始前正在播放广告");
        System.out.println(method.getName());
        System.out.println("电影结束了，接续播放广告");
        return null;
    }
}
