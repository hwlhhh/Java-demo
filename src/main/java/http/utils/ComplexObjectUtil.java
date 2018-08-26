package http.utils;

import http.entity.ComplexObject;
import http.entity.Father;
import http.entity.Son;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HWL on 2018/7/29
 */
public class ComplexObjectUtil {

    public static ComplexObject getComplexObject(){

        Father father = new Father();
        father.setName("张三");
        father.setAge("30");
        father.setSalary("12000");

        Son son01 = new Son();
        son01.setName("张四");
        son01.setAge("3");
        Son son02 = new Son();
        son02.setName("张五");
        son02.setAge("2");

        List<Son> list = new ArrayList<>();
        list.add(son01);
        list.add(son02);
        father.setSonList(list);

        return father;

    }
}
