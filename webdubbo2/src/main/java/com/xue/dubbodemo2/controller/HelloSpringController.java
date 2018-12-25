package com.xue.dubbodemo2.controller;


import com.xue.dubbodemo.bean.UserBean;
import com.xue.dubbodemo.service.HellowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.LastModified;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class HelloSpringController implements LastModified {
    @Autowired
    private HellowService hellowService;

    String message = "Welcome to Spring MVC!";

    private long lastModified;

    @RequestMapping("/hello")
    public ModelAndView showMessage(@RequestParam(value = "name", required = false, defaultValue = "Spring") String
                                            name) {

        ModelAndView mv = new ModelAndView("hellospring");//指定视图
        //向视图中添加所要展示或使用的内容，将在页面中使用
        mv.addObject("message", message);
        mv.addObject("name", name);
        System.out.println("----showMessage is running----");
        return mv;
    }

    @RequestMapping("/hello2")
    @ResponseBody
    public UserBean showMessage2(@RequestParam(value = "name", required = false, defaultValue = "Spring") String
                                         name) {
        hellowService.sayHellow(name);

        return new UserBean("张11安", 34);
    }

    @Override
    public long getLastModified(HttpServletRequest request) {
        if (lastModified == 0L) {
            //如果内容有更新，应该重新返回内容最新修改的时间戳
            lastModified = System.currentTimeMillis();
        }
        return lastModified;
    }
}