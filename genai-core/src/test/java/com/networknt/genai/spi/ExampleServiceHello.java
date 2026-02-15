package com.networknt.genai.spi;

public class ExampleServiceHello implements ExampleService{
    @Override
    public String getGreeting() {
        return "Hello";
    }
}
