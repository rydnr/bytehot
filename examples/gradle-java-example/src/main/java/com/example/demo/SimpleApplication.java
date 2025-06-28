package com.example.demo;

/**
 * Simple application for testing ByteHot Gradle plugin without Spring Boot.
 */
public class SimpleApplication {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ByteHot Gradle Plugin Demo ===");
        System.out.println("Simple application started successfully!");
        
        for (int i = 0; i < 5; i++) {
            System.out.println("Hello from ByteHot Gradle! Iteration: " + i + ", Time: " + System.currentTimeMillis());
            Thread.sleep(2000);
        }
        
        System.out.println("Application completed successfully.");
    }
}