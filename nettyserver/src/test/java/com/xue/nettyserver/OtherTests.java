package com.xue.nettyserver;

import com.xue.nettyserver.websocket.HttpRequestHandler;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/4/28 15:49
 * @Description:
 */
public class OtherTests {
    /**
     *
     */
    @Test
    public  void demo(){
        URL location = HttpRequestHandler.class
                .getProtectionDomain()
                .getCodeSource().getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            File INDEX = new File(path);
            System.out.println("demo");
        } catch (URISyntaxException e) {
            throw new IllegalStateException(
                    "Unable to locate index.html", e);
        }
    }
}
