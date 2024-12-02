package com;

public class ConfigLoader {
    public static String getApiKey() {
        // Retrieve the API key from the environment variables
        String apiKey = System.getenv("OPENAI_API_KEY");
        
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API key not found. Ensure OPENAI_API_KEY is set in your environment variables.");
        }

        return apiKey;
    }
}