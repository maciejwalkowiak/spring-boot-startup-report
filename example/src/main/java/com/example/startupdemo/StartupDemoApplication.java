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