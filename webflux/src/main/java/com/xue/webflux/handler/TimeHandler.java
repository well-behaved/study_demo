package com.xue.webflux.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * WebFlux的函数式开发模式
 * <p>
 * 创建统一存放处理时间的Handler类
 */
@Component
public class TimeHandler {
    /**
     * 返回当前时分秒
     *
     * @param
     * @return
     * @author xuexiong@souche.com  22:12 2020-09-24
     **/
    public Mono<ServerResponse> getTime(ServerRequest serverRequest) {
        return ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Now is " + new SimpleDateFormat("HH:mm:ss").format(new Date())), String.class);
    }

    /**
     * 返回当前年月日
     *
     * @param
     * @return
     * @author xuexiong@souche.com  22:12 2020-09-24
     **/
    public Mono<ServerResponse> getDate(ServerRequest serverRequest) {
        return ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Today is " + new SimpleDateFormat("yyyy-MM-dd").format(new Date())), String.class);
    }

    /**
     * 每秒推送时间
     * @author xuexiong@souche.com  22:15 2020-09-24
     * @param 
     * @return 
     **/
    public Mono<ServerResponse> sendTimePerSec(ServerRequest serverRequest) {
        Mono<ServerResponse> body = ok().contentType(MediaType.TEXT_EVENT_STREAM).body(  // 1
                Flux.interval(Duration.ofSeconds(1)).   // 2
                        map(l -> new SimpleDateFormat("HH:mm:ss").format(new Date())),
                String.class);
        System.out.println("over");
        return body;
    }
}