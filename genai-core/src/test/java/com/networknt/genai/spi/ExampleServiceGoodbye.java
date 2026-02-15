package com.networknt.genai.spi;

public class ExampleServiceGoodbye implements ExampleService{
    @Override
    public String getGreeting() {
        return "Goodbye";
    }
}
