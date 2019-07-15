package com.xue.otherdemo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/5/14 20:05
 * @Description:
 */
public class demo {
    public static void main(String[] args) throws InterruptedException {
        //2019-05-14 12:14:51
        Date date = new Date(1557807291000L);
        Thread.sleep(3000);
        // 2019/5/13 20:12:37
        Date date2 = new Date(1557749497000L);

        System.out.println(ChronoUnit.DAYS.between(date.toInstant(),date2.toInstant()));
        System.out.println(ChronoUnit.DAYS.between(date2.toInstant(),date.toInstant()));
        System.out.println();
        System.out.println(1000*60*60*24);
    }
}
