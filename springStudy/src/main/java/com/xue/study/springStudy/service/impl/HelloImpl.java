package com.xue.study.springStudy.service.impl;


import com.xue.study.springStudy.service.IHello;

public class HelloImpl implements IHello {
	private String name;
	
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
