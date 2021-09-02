package com.xue.otherdemo.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: xuexiong@souche.com
 * @date: 2021/9/1 15:31
 * @description:
 */
public class HashMapDemo {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("Aa", "valuessss");
        map.put("BB", "valuessss");
        map.remove("Aa");
        System.out.println("Aa".hashCode());
        System.out.println("BB".hashCode());



    }
}
