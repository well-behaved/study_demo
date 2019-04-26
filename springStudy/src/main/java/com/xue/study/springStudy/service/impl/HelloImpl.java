package com.xue.study.springStudy.service.impl;


import com.xue.study.springStudy.service.IHello;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class HelloImpl implements IHello {
	private String name;
	/*
	单例测试
	 */
	private List<String> listTest;
	@Override
	public void listTest(){
		if(CollectionUtils.isEmpty(listTest)){
			listTest=new ArrayList<>();
		}
		listTest.add("xuedemo");
	}
	
	public String sayHellow(){
		String hello="你好:";
		return name==null?"怎么没人啊":hello+name ;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
	
}
