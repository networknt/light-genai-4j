package com.networknt.agent.spi;

public class ExampleServiceGoodbye implements ExampleService{
    @Override
    public String getGreeting() {
        return "Goodbye";
    }
}
