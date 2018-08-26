package http.entity;

import java.util.List;

/**
 * Created by HWL on 2018/7/25
 */
public class Father extends ComplexObject{

    private String name;
    private String age;
    private String salary;
    private List<Son> sonList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public List<Son> getSonList() {
        return sonList;
    }

    public void setSonList(List<Son> sonList) {
        this.sonList = sonList;
    }
}
