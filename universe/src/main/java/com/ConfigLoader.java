package com;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    public static String getApiKey() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty("OPENAI_API_KEY");
    }
}