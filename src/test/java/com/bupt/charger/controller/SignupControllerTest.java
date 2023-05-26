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
import com.bupt.charger.TestUtils;

/**
 * @author ll （ created: 2023-05-26 20:01 )
 */
class SignupControllerTest {
    private static final String SIGNUP_URL = "http://localhost:6480/signup";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";
    private static final String CAR_ID = "test";


    @Test
    void test() {
        testLogin(USERNAME, PASSWORD, CAR_ID);
        testLogin(USERNAME, "already", CAR_ID);
    }

    void testLogin(String username, String password, String car_id) {
        try {
            // 设置请求体参数
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(Map.of("username", username, "password", password, "car_id", car_id));
            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);

            // 发送请求并获取响应
            String responseBody = TestUtils.sendPostRequest(SIGNUP_URL, requestBodyBytes);
            System.out.println(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}