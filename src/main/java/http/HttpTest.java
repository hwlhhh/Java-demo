package http;


import com.fasterxml.jackson.databind.ObjectMapper;
import http.entity.ComplexObject;
import http.utils.ComplexObjectUtil;
import http.utils.HttpClientUtil;

import java.util.Map;

public class HttpTest implements Runnable {

    String url = "http://localhost:8071/api/zt/v1/mysql/test";
    ComplexObject obj = ComplexObjectUtil.getComplexObject();
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void run() {
        Map<String, String> response = null;
        try {
            String jsonObj = mapper.writeValueAsString(obj);
            response = HttpClientUtil.sendHttpPostJson(url, jsonObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("statusCode: " + response.get("statusCode"));
        System.out.println("responseContent: " + response.get("content"));
        System.out.println("----------------");
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 9; i++) {
            System.out.println(Thread.currentThread().getName() + " " + i);
            HttpTest thread = new HttpTest();
            new Thread(thread, "线程" + i).start();
        }

    }


}
