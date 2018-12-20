package com.xue.study.springStudy.aop.JdkProxy;

import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.lang.reflect.Proxy;

/**
 * JDK动态代理是基于接口的代理，下面举例说明
 * <p>
 * 代理类：proxy，代理动作必须要基于一个proxy实例来执行
 * 代理执行类：实现InvocationHandler，
 * 被代理类：基于接口的用户自己的方法，
 * 首先说明下InvocationHandler的invoke
 * <p>
 * public interface InvocationHandler {
 * 　　public Object invoke(Object proxy, Method method, Object[] args)
 * 　　throws Throwable;
 * }
 * proxy：方法基于哪个proxy实例来执行
 * <p>
 * method：执行proxy实例的哪个方法（proxy代理谁就执行谁的方法）
 * <p>
 * args：methed的入参
 *
 * @Auther: xuexiong@souche.com
 * @Date: 2018/12/13 14:22
 * @Description:
 */
public class JdkProxyMain {
    public static void main(String[] args) {
        /*
        对比代码
         */
        Say say1 = new SayImpl();
        /*
        执行的代码
         */
        TestInvocationHandler testInvocationHandler = new TestInvocationHandler(new SayImpl());
        Say say = (Say) Proxy.newProxyInstance(SayImpl.class.getClassLoader(), SayImpl.class.getInterfaces(),
                testInvocationHandler);
        say.sayHello("my dear");
        /*
        下面是上面实现原理的一部分--测试失败  详细逻辑待研究（具体如何通过编程的方式生成）
         */
        byte[] classFile = ProxyGenerator.generateProxyClass("$Proxy0", Say.class.getInterfaces());
        String path = "./SayProxy.class";
        try(FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(classFile);
            fos.flush();
            System.out.println("代理类class文件写入成功");
        } catch (Exception e) {
            System.out.println("写文件错误");
        }
    }

}
