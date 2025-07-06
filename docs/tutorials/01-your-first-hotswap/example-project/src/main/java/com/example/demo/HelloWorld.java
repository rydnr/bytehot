package com.example.demo;

/**
 * A simple application to demonstrate ByteHot hot-swapping
 * 
 * This class serves as the entry point for your first hot-swap tutorial.
 * You'll modify the displayMessage() method while the application is running
 * to see ByteHot's hot-swapping capabilities in action.
 */
public class HelloWorld {
    
    private String message = "Hello, World!";
    private int counter = 0;
    
    /**
     * Main application loop that displays messages every 2 seconds
     */
    public void run() {
        System.out.println("=== ByteHot Demo Application Started ===");
        System.out.println("💡 Keep this running and modify the code in another terminal!");
        System.out.println("🔥 Hot-swap the displayMessage() method to see changes instantly!");
        System.out.println();
        
        while (true) {
            displayMessage();
            
            try {
                Thread.sleep(2000); // Wait 2 seconds between messages
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * This method will be hot-swapped during the demo.
     * 
     * Try modifying this method while the application is running:
     * 1. Change the output format
     * 2. Add emojis or decorations
     * 3. Include timestamps
     * 4. Add conditional logic based on counter
     * 
     * After making changes, run: mvn compile
     * Your changes will appear immediately!
     */
    public void displayMessage() {
        counter++;
        System.out.printf("[%d] %s%n", counter, message);
    }
    
    /**
     * Gets the current message.
     * This method can also be hot-swapped!
     * 
     * @return the current message string
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets a new message.
     * Try hot-swapping this to change the message format!
     * 
     * @param newMessage the new message to display
     */
    public void setMessage(final String newMessage) {
        this.message = newMessage;
    }
    
    /**
     * Gets the current counter value
     * 
     * @return the number of messages displayed so far
     */
    public int getCounter() {
        return counter;
    }
    
    /**
     * Application entry point
     */
    public static void main(String[] args) {
        System.out.println("🚀 Starting ByteHot Tutorial 01: Your First Hot-Swap");
        System.out.println("📖 Follow along at: docs/tutorials/01-your-first-hotswap/README.md");
        System.out.println();
        
        new HelloWorld().run();
    }
}