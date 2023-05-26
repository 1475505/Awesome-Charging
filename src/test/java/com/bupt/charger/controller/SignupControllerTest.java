package com.bupt.charger.controller;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author ll （ created: 2023-05-26 20:01 )
 */
class SignupControllerTest {
    public static void main(String[] args) {

        try {
            // 请求的URL
            URL url = new URL("http://localhost:8080/signup");

            // 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 设置请求体参数
            String username = "testUser";
            String password = "testPassword";
            String car_id = "AF_1234";
            String requestBody = "username=" + username + "&password=" + password + "&car_id=" + car_id;
            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(requestBodyBytes.length));

            // 发送请求体数据
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBodyBytes);
            outputStream.flush();
            outputStream.close();

            // 获取响应
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 可以根据需要获取响应内容等
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
