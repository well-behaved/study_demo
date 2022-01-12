package com.xue.otherdemo.timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: xuexiong@souche.com
 * @date: 2022/1/10 11:27
 * @description:
 */
public class TimmerDemo {

    public static void main(String[] args) {

        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("executing now!");
            }
        };
        TimerTask task2 = new TimerTask() {
            public void run() {
                System.out.println("task2 executing now!" + new Date());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        TimerTask task3 = new TimerTask() {
            public void run() {
                System.out.println("task3 executing now!" + new Date());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // 延迟 1s 打印一次
        Timer timer = new Timer();
        timer.schedule(task, 1000);
        // 延迟 1s 固定时延每隔 1s 周期打印一次
        Timer timer2 = new Timer();
        timer2.schedule(task2, 1000, 1000);
        // 延迟 1s 固定速率每隔 1s 周期打印一次
        Timer timer3 = new Timer();
        timer3.scheduleAtFixedRate(task3, 1000L, 1000L);
    }
}
