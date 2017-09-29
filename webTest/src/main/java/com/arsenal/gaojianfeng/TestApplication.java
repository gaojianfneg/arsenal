package com.arsenal.gaojianfeng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        System.out.println("run");
        SpringApplication.run(TestApplication.class, args);
    }
}
