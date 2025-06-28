package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting ByteHot Gradle Example Application...");
        
        SpringApplication.run(Application.class, args);
        
        // Keep application running for demo purposes
        while (true) {
            System.out.println("Hello from ByteHot Gradle! Current time: " + System.currentTimeMillis());
            Thread.sleep(3000);
        }
    }
}