package com.bupt.charger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author ll （ created: 2023-05-26 20:01 )
 */
class SignupControllerTest {
    private static final String SIGNUP_URL = "http://localhost:6480/signup";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";
    private static final String CAR_ID = "test";


    @Test
    void test(){
        testLogin(USERNAME, PASSWORD, CAR_ID);
    }

    void testLogin(String username, String password, String car_id) {
        try {
            // 设置请求体参数
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(Map.of("username", username, "password", password, "car_id", car_id));
            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);

            // 发送请求并获取响应
            String responseBody = sendPostRequest(SIGNUP_URL, requestBodyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String sendPostRequest(String url, byte[] requestBodyBytes) throws Exception {
        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
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