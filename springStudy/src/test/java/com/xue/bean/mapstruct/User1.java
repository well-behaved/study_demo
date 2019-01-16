package com.xue.bean.mapstruct;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/9/26 09:55
 * @Description:
 */
public class User1 {
    @Test
    public void demo(){
        List<String> list= new ArrayList<>(4);
        List<String> list2= new ArrayList<>(4);
        list.add("123123");
        list.add("1231fer23");
        list.add("1231qwer23");
        list.add("1231qwerqwe23");
        list.forEach(string->{
            try {
                Thread.sleep(1000);
                list2.add("asdf");
                System.out.println(string);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


//        Stream.generate(()->new Random().nextInt(100)).forEach(
//                integer -> {
//                    try {
//                        Thread.sleep(1000);
//                        System.out.println(integer);
//                    } catch (InterruptedException e) {
//                        System.out.println(e.getMessage());
//                    }
//                }
//        );
    }
}
