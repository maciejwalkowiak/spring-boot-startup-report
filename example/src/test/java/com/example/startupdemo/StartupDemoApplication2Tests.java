package com.example.startupdemo;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class StartupDemoApplication2Tests {

    @Test
    void contextLoads2() throws IOException {
        System.out.println("xx");
    }

}
