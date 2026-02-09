package com.networknt.agent.spi;

public class ExampleServiceHello implements ExampleService{
    @Override
    public String getGreeting() {
        return "Hello";
    }
}
