package com.xue.otherdemo.collection;

/**
 * @author: xuexiong@souche.com
 * @date: 2021/9/2 10:30
 * @description:
 */
public class ConcurrentHashMap {
    public static void main(String[] args) {
        java.util.concurrent.ConcurrentHashMap<String, String> currenthashMap
                = new java.util.concurrent.ConcurrentHashMap<>();
        currenthashMap.put("Aa", "valuessss");
        currenthashMap.put("BB", "valuessss");
    }
}
