package javademo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HWL on 2018/7/24
 */
public class Demo {

    public void test01() {

        List<Map> list1 = new ArrayList<>();

        HashMap<String, Object> hm01 = new HashMap<>();
        hm01.put("key01", "value01");
        hm01.put("key02", 2);
        hm01.put("key03", "value03");
        hm01.put("key04", "value04");
        hm01.put("key05", "value05");

        HashMap<String, Object> hm02 = new HashMap<>();
        hm02.putAll(hm01);

        list1.add(hm01);
        list1.add(hm02);

//        print(list1);
//
//        List<Map<String, Object>> list2 = new ArrayList<>();
//
//        list2.addAll(list1);
    }

//    public static void main(String[] args) {
//
//    }


}
