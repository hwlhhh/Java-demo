package json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.entity.Father;
import json.entity.Son;

import java.io.IOException;
import java.util.List;

public class JacksonDemo {

    private static String jsonStr = "{\"name\":\"张三\",\"age\":\"30\",\"salary\":\"12000\"," +
            "\"sonList\":[{\"name\":\"张四\",\"age\":\"3\"},{\"name\":\"张五\",\"age\":\"2\"},{\"name\":\"张六\",\"age\":\"1\"}]}";

    public static void main(String[] args) {
        parseJson1(jsonStr);
        parseJson2(jsonStr);
    }

    /**
     * 解析Json串
     *
     * @param jsonStr
     */
    private static void parseJson1(String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(jsonStr);

            String name = rootNode.path("name").asText();
            int age = rootNode.path("age").asInt();
            Long salary = rootNode.path("salary").asLong();
            JsonNode sonList = rootNode.path("sonList");

            System.out.println("name: " + name);
            System.out.println("age: " + age);
            System.out.println("salary: " + salary);
            System.out.println("sonList: " + sonList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将Json串转成相应对象
     *
     * @param jsonStr
     */
    private static void parseJson2(String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Father father = mapper.readValue(jsonStr, Father.class);

            String name = father.getName();
            String age = father.getAge();
            String salary = father.getSalary();
            List<Son> sonList = father.getSonList();

            System.out.println("father: " + name);
            System.out.println("age: " + age);
            System.out.println("salary: " + salary);
            System.out.println("-------- " + name + "的儿子们 --------");
            for (Son son : sonList) {
                System.out.println(son.getName());
                System.out.println(son.getAge());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
