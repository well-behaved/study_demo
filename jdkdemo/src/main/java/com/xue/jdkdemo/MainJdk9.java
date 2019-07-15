package com.xue.jdkdemo;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @Author: xuexiong@souche.com
 * @Date: 2019-06-28 09:52
 * @Description:
 */
public class MainJdk9 {
    public static void main(String[] args) {
        System.out.println("----开始运行");
        /*
        集合工厂方法
        从工厂方法返回已放入数个元素的集合实现是高度优化的。这是可能的，因为它们是不可变的：
        在创建后，继续添加元素到这些集合会导致 “UnsupportedOperationException” 。
         */
        try {
            factory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
            改进的 Stream API
         */
        /*长期以来，Stream API 都是 Java 标准库最好的改进之一。通过这套 API 可以在集合上建立用于转换的申明管道。
        在 Java 9 中它会变得更好。Stream 接口中添加了
        4 个新的方法：dropWhile, takeWhile, ofNullable。
        还有个 iterate 方法的新重载方法，可以让你提供一个 Predicate (判断条件)来指定什么时候结束迭代：

        takeWhile
        这个方法用于对Stream流进行一个条件过滤，返回过滤后的流。该方法是一个默认方法。:这个方法用于对Stream流进行一个条件过滤，返回过滤后的流
        。该方法是一个默认方法。
        filter will remove all items from the stream that do not satisfy the condition.
        takeWhile will abort the stream on the first occurrence of an item which does not satisfy the condition.

        dropWhile 删除首次不满足条件的用户
        */
        IntStream.iterate(1, i -> i < 2, i -> i + 1).forEach(System.out::print);
        System.out.println();
        List<Integer> lists = new ArrayList<>();
        lists.add(1);
        lists.add(2);
        lists.add(3);
        lists.add(4);
        lists.add(5);
        lists.add(6);
        lists.add(1);
        lists.stream().takeWhile(num -> num < 2).forEach(System.out::print);
        System.out.println();
        lists.stream().filter(num -> num < 2).forEach(System.out::print);
        System.out.println();
        lists.stream().dropWhile(num -> num < 2).forEach(System.out::print);
        System.out.println();
        /*
        接口可以有私有方法
         */
        InterfaceDemo interfaceDemo = new InterfaceDemo() {
        };
        interfaceDemo.init();
        /*
        在JDK8中，新增了try-with-resources语句，你可以在try后的括号中初始化资源，可以实现资源自动关闭
        在JDK9中，改进了try-with-resources语句，可以在try外进行初始化，在括号内引用，即可实现资源自动关闭
         */
        //1.8
        try (InputStreamReader reader = new InputStreamReader(System.in)) {
            //......
        } catch (IOException e) {
            e.printStackTrace();
        }
        //1.9
        InputStreamReader reader = new InputStreamReader(System.in);
        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        //多资源用分号隔开
        try (reader;writer) {
            //......
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 限制使用单独下划线标识符
//        String _ = "12";
//        System.out.println(_);
        /*
        从很多不同应用程序收集的信息表名，字符串是堆使用的主要组成部分，而且，大多数字符串对象只包含一个字符，这样的字符只需要一个字节的存储空间
        ，因此这些字符串对象的内部char数组中有一半的空间被闲置。
        JDK9之前String底层使用char数组存储数据private final char value[]
        ，JDK9将String底层存储数据改为byte数组存储数据private final byte[] value。
         */
        String string = new String();







        System.out.println("-----结束运行");
    }

    private static void factory() {
        System.out.println("factory----开始运行");
        Set<Integer> sets = Set.of(1, 2, 3, 4);
        List<String> lists = List.of("first", "second");
        System.out.println(JSONObject.toJSON(sets));
        System.out.println(JSONObject.toJSON(lists));
        //修改一下会抛出异常
        sets.add(6);
        System.out.println("factory----结束运行");
    }
    private interface InterfaceDemo{
        private void init(){
            System.out.println("这是接口的私有方法");
        }
    }
}
