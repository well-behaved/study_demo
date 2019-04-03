package com.xue.otherdemo.main;

import com.xue.otherdemo.bean.ClassBean;
import com.xue.otherdemo.bean.StudentBean;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


public class ObjectStreamDemo1 {
    /**
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public final static String PATH = "/xuexiong/obj.object1";


    public static void main(String[] args) throws IOException,
            ClassNotFoundException {
        writeObj();
        readObj();
        System.out.println("--End--");
    }

    private static void readObj() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                PATH));
        StudentBean p = (StudentBean) ois.readObject();
        System.out.println(p);
    }

    private static void writeObj() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                PATH));
        StudentBean studentBean = new StudentBean();
        Map map = new HashMap();
        map.put("name", "xuexiong_map");
        HashMap hashMap = new HashMap();
        hashMap.put("name", "xuexiong_hashMap");
        ClassBean classBean = new ClassBean();
        classBean.setClassName("xuexiong_class");
        studentBean.setMap(map);
        studentBean.setHashMap(hashMap);
        studentBean.setClassBean(classBean);
        oos.writeObject(studentBean);
        oos.close();
    }
}