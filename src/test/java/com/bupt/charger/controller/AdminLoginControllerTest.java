package com.bupt.charger.controller;

import com.bupt.charger.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 @author wxl,wyf （ created: 2023-05-29 15:26 )
 */
public class AdminLoginControllerTest {


    private static final String LOGIN_URL = "http://localhost:6480/admin/login";
    private static final String PASSWORD = "test";
    private static final String ADMINNAME = "admin";
    @Test
    void admintest(){
        testAdminLogin(ADMINNAME, PASSWORD);
        testAdminLogin(ADMINNAME, "wrong_password");
    }

    void testAdminLogin(String adminName, String password) {
        try {
            // 设置请求体参数
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(Map.of("adminName", adminName, "password", password));
            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);

            // 发送请求并获取响应
            String responseBody = TestUtils.sendPostRequest(LOGIN_URL, requestBodyBytes);
            System.out.println(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
