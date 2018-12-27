package com.xue.dubbodemo.service.impl;

import com.alibaba.dubbo.rpc.RpcContext;
import com.xue.dubbodemo.listeren.CallbackListener;
import com.xue.dubbodemo.service.HellowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/10/24 15:15
 * @Description:
 */
@Service("hellowService")
@Slf4j
public class HellowServiceImpl implements HellowService {
    @Override
    public String sayHellow(String name) {
        /**
         * 服务日至
         */
        // 本端是否为消费端，这里会返回true
        boolean isConsumerSide = RpcContext.getContext().isConsumerSide();
        // 获取最后一次调用的提供方IP地址
        String serverIP = RpcContext.getContext().getRemoteHost();
        // 获取当前服务配置信息，所有配置信息都将转换为URL的参数
        String application = RpcContext.getContext().getUrl().getParameter("application");
        // 注意：每发起RPC调用，上下文状态会变化
//        yyyService.yyy();
        log.info("isConsumerSide----:"+isConsumerSide);
        log.info("serverIP----"+serverIP);
        log.info("application----"+application);

        /**
         *  获取客户端隐式传入的参数，用于框架集成，不建议常规业务使用
         */
        String index = RpcContext.getContext().getAttachment("name1");
        log.info("index----"+index);
        return "你好" + name;
    }
    @Override
    public void callback(CallbackListener listeren) {
        System.out.println("HellowServiceImpl.customer----");
        listeren.callBack(System.currentTimeMillis());
    }
}
