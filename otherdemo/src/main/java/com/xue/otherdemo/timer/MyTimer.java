package com.xue.otherdemo.timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/3/14 10:21
 * @Description:
 */
public class MyTimer {
    public static void main(String[] args) {
        Timer timer2 = new Timer();
        //前一次程序执行开始 后 2000ms后开始执行下一次程序
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("begin");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("循环执行-前一次程序执行开始 后 2000ms后开始执行下一次程序 IMP 当前时间" + this.scheduledExecutionTime());
            }
        }, 0, 2000);

//        commonTimer();
//        ScheduledExecutorDemo();
    }

    /**
     * 延时执行的线程池ScheduledExecutorService   一般替代timmer的类
     */
    private static void ScheduledExecutorDemo() {
        //ScheduledThreadPoolExecutor
        ScheduledExecutorService service = Executors.newScheduledThreadPool(3);
        System.out.println("开始任务");
        //延时3秒执行
        service.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("执行任务");
            }
        }, 3, TimeUnit.SECONDS);
    }

    /**
     * jdk timmer  一个出错那么就停止运行
     */
    private static void commonTimer() {
        Timer timer = new Timer();
        /*
         *只执行一次
         */
        //延迟1000ms执行程序
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("只执行一次---延迟1000ms执行程序 IMP 当前时间" + this.scheduledExecutionTime());
            }
        }, 1000);
        //延迟10000ms执行程序
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("只执行一次---延迟10000ms执行程序 IMP 当前时间" + this.scheduledExecutionTime());
            }
        }, new Date(System.currentTimeMillis() + 10000));

        Timer timer2 = new Timer();
        /*
         *循环执行
         */
        //前一次执行程序结束后 2000ms 后开始执行下一次程序
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("循环执行-前一次执行程序结束后 2000ms 后开始执行下一次程序 IMP 当前时间" + this.scheduledExecutionTime());
            }
        }, 0, 2000);

        //前一次程序执行开始 后 2000ms后开始执行下一次程序
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("循环执行-前一次程序执行开始 后 2000ms后开始执行下一次程序 IMP 当前时间" + this.scheduledExecutionTime());
            }
        }, 0, 2000);
    }
}
