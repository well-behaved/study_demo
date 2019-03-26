package com.xue.otherdemo.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/3/14 16:14
 * @Description:
 */
public class MyThreadPool {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.SECONDS, new
                ArrayBlockingQueue(33), new MyThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("nihao");
            }
        };
        threadPoolExecutor.execute(runnable);


    }
}
