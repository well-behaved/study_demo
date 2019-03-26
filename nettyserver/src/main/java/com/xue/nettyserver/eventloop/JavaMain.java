package com.xue.nettyserver.eventloop;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/3/18 10:40
 * @Description:
 */
public class JavaMain {

    public static void main(String[] args) throws InterruptedException {
        /*
        java定时器  5秒后运行
         */
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
        ScheduledFuture<?> future = executor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("5 seconds later"); }
        }, 5, TimeUnit.SECONDS);
        Thread.sleep(6*1000);
        executor.shutdown();
    }
}
