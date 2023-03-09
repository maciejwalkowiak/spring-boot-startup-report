package com.example.startupdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class StartupDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartupDemoApplication.class, args);
    }

}

@Service
class FooService {

}
//
//interface PersonRepository extends JpaRepository<Person, String> {}
//
//@Entity
//class Person {
//    @Id
//    private String id;
//    private String name;
//
//    String getId() {
//        return id;
//    }
//
//    void setId(String id) {
//        this.id = id;
//    }
//
//    String getName() {
//        return name;
//    }
//
//    void setName(String name) {
//        this.name = name;
//    }
//}
