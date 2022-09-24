package com.cloud.cloudclient.utils;

import java.io.*;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (var inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PropertiesUtil() {
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        PROPERTIES.setProperty(key, value);
        try (OutputStreamWriter out =
                     new OutputStreamWriter(new FileOutputStream("src/main/resources/application.properties"))) {
            PROPERTIES.store(out, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
