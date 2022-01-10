package com.xue.otherdemo.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author: xuexiong@souche.com
 * @date: 2021/10/27 18:03
 * @description: future例子
 */
public class MyFutureDemo {

    public static void main(String args[]) throws Exception {
        // 使用
        ExecutorService executor = Executors.newCachedThreadPool();
        MyTask task = new MyTask();
        Future<Integer> result = executor.submit(task);
        // 注意调用get方法会阻塞当前线程，直到得到结果。
        // 所以实际编码中建议使用可以设置超时时间的重载get方法。
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1300);
            System.out.println(Thread.currentThread().getName() + ":" + i);
        }
        System.out.println(result.get());
        executor.shutdown();
    }

    private static class MyTask implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
            return 111;
        }
    }
}
