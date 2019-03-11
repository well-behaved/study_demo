package com.xue.nettyserver.hello;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/3/11 11:10
 * @Description:
 */
public class LiveMessage {
    private int length;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
