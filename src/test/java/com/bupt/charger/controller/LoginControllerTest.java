package com.bupt.charger.controller;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.net.URL;


import static org.junit.jupiter.api.Assertions.*;
/**
@author ll （ created: 2023-05-26 20:05 )
*/
class LoginControllerTest {

    private static final String LOGIN_URL = "http://localhost:8080/login";
    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "testPassword";

    @Test
    void test(){
        testLogin(USERNAME, PASSWORD);
    }
    
    void testLogin(String username, String password) {
        try {
            // 设置请求体参数
            String requestBody = "username=" + username + "&password=" + password;
            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);

            // 发送请求并获取响应
            String responseBody = sendPostRequest(LOGIN_URL, requestBodyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String sendPostRequest(String url, byte[] requestBodyBytes) throws Exception {
        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(requestBodyBytes.length));

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(requestBodyBytes);
            outputStream.flush();
        }

        // 获取响应
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // 读取响应内容
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}