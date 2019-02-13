package com.xue.dubbodemo2.controller;


import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.service.EchoService;
import com.xue.dubbodemo.bean.UserBean;
import com.xue.dubbodemo.bean.ValidationParameter;
import com.xue.dubbodemo.listeren.CallbackListener;
import com.xue.dubbodemo.service.HellowService;
import com.xue.dubbodemo.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.LastModified;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Controller
@Slf4j
public class HelloSpringController implements LastModified {



    @Autowired
    @Qualifier("callBackService")
    private HellowService callBackService;
    @Autowired
    private HellowService hellowService;
    @Autowired
    @Qualifier("hellowService3")
    private HellowService hellowService3;
    @Autowired
    private ValidationService validationService;

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

        UserBean userBean = new UserBean();
        /*
        回声测试用于检测服务是否可用，回声测试按照正常请求流程执行，能够测试整个调用是否通畅，可用于监控。

        所有服务自动实现 EchoService 接口，只需将任意服务引用强制转型为 EchoService，即可使用。
         */
        EchoService echoService = (EchoService) hellowService; // 强制转型为EchoService
        // 回声测试可用性
        Object status = echoService.$echo("OK");
        log.info("回声测试：--{}", status);
        userBean.setName(hellowService.sayHellow(name));
        /*
        可以通过 RpcContext 上的 setAttachment 和 getAttachment 在服务消费方和提供方之间进行参数的隐式传递。
        设置的 KV 对，在完成下面一次远程调用会被清空，即多次远程调用要多次设置。
         */
        RpcContext.getContext().setAttachment("name1", "薛雄"); // 隐式传参，后面的远程调用


        return userBean;
    }

    @RequestMapping("/callback")
    @ResponseBody
    public UserBean callback(@RequestParam(value = "name", required = false, defaultValue = "Spring") String
                                     name) {

        UserBean userBean = new UserBean();
        callBackService.callback((CallbackListener & Serializable) (customerSystem) -> {
            System.out.println("输出消费者的时间：" + customerSystem);
        });


        return userBean;
    }

    @RequestMapping("/check")
    @ResponseBody
    public UserBean showMessage3(@RequestParam(value = "name", required = false, defaultValue = "Spring") String
                                         name) {
        ValidationParameter validationParameter = new ValidationParameter();
        validationParameter.setName("学大先生");
        try {
            validationService.update(validationParameter);
        } catch (Exception e1) {
            log.warn("getMessage-----" + e1.getMessage());
            log.warn("getLocalizedMessage-----" + e1.getLocalizedMessage());
            log.error("update", e1);
        }
        try {
            validationService.save(validationParameter);
        } catch (RpcException e2) {
            log.info("getCode-----" + Integer.toString(e2.getCode()));
            log.warn(e2.getMessage());
            log.warn(e2.getLocalizedMessage());
            log.error("save", e2);
        }
        try {
            validationService.update(validationParameter);
        } catch (Exception e3) {
            log.warn(e3.getMessage());
            log.warn(e3.getLocalizedMessage());
            log.error("update", e3);
        }

        UserBean userBean = new UserBean();
        userBean.setName(hellowService.sayHellow(name));
        return userBean;
    }

    @RequestMapping("/async")
    @ResponseBody
    public UserBean showMessage4(@RequestParam(value = "name", required = false, defaultValue = "Spring22") String
                                         name) throws ExecutionException, InterruptedException {

        UserBean userBean = new UserBean();
        // 此调用会立即返回null
        hellowService3.sayHellow("111111");
        // 拿到调用的Future引用，当结果返回后，会被通知和设置到此Future
        Future<String> fooFuture = RpcContext.getContext().getFuture();

        // 此调用会立即返回null
        hellowService3.sayHellow("222222");
        // 拿到调用的Future引用，当结果返回后，会被通知和设置到此Future
        Future<String> barFuture = RpcContext.getContext().getFuture();

        // 此时findFoo和findBar的请求同时在执行，客户端不需要启动多线程来支持并行，而是借助NIO的非阻塞完成

        // 如果foo已返回，直接拿到返回值，否则线程wait住，等待foo返回后，线程会被notify唤醒
        String foo = fooFuture.get();
        // 同理等待bar返回
        String bar = barFuture.get();
        // 如果foo需要5秒返回，bar需要6秒返回，实际只需等6秒，即可获取到foo和bar，进行接下来的处理。
        userBean.setName(foo + bar);
        return userBean;
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