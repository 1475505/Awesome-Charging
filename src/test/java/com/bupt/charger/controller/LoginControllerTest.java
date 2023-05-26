package com.bupt.charger.controller;

import com.bupt.charger.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;
/**
@author ll （ created: 2023-05-26 20:05 )
*/
class LoginControllerTest {

    private static final String LOGIN_URL = "http://localhost:6480/login";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";

    @Test
    void test(){
        testLogin(USERNAME, PASSWORD);
        testLogin(USERNAME, "wrong_password");
    }
    
    void testLogin(String username, String password) {
        try {
            // 设置请求体参数
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(Map.of("username", username, "password", password));
            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);

            // 发送请求并获取响应
            String responseBody = TestUtils.sendPostRequest(LOGIN_URL, requestBodyBytes);
            System.out.println(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}