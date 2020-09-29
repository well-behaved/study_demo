package com.xue.webflux;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by fangxiao on 2017/3/24.
 */
@RestController
public class Controller {

    @RequestMapping("/helloworld")
    public String helloWorld() {
        return "helloworld";
    }
}