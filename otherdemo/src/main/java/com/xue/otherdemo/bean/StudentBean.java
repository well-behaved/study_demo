package com.xue.otherdemo.bean;

import java.beans.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/4/1 10:21
 * @Description:
 */

public class StudentBean implements Serializable {
    private static final long serialVersionUID = -640380096016055642L;
    private  Map map;
    private HashMap hashMap;
    private ClassBean classBean;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public ClassBean getClassBean() {
        return classBean;
    }
    public void setClassBean(ClassBean classBean) {
        this.classBean = classBean;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public HashMap getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap hashMap) {
        this.hashMap = hashMap;
    }
}
