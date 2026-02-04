package com.networknt.genai.antigravity;

import com.networknt.genai.ChatMessage;
import com.networknt.genai.RequestOptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class AntigravityClientTest {

    @Test
    @Disabled("Requires manual authentication flow in browser")
    public void testGeminiChat() {
        AntigravityClient client = new AntigravityClient();
        
        // Use RequestOptions to specify a Gemini model as requested
        RequestOptions options = new RequestOptions();
        options.setModel("google-antigravity/claude-opus-4-5-thinking");
        
        ChatMessage message = new ChatMessage();
        message.setRole("user");
        message.setContent("Hello, are you a Gemini model?");
        List<ChatMessage> messages = Collections.singletonList(message);
        
        System.out.println("Sending request to Antigravity...");
        // This will trigger the Auth flow if no token is cached.
        // Check console output for the Auth URL.
        String response = client.chat(messages, options);
        
        System.out.println("Response from Antigravity:");
        System.out.println(response);
        
        if (response != null && !response.startsWith("Error")) {
            System.out.println("Test Passed!");
        } else {
            System.out.println("Test Failed or Error occurred.");
        }
    }
}
