package hellow.servlet.basic;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter  // Lombok으로 getter setter 설정을 생략 가능하다
public class HellowData {

    private String username;
    private int age;

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }
}
