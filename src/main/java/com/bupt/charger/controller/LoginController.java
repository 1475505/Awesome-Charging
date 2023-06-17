package com.bupt.charger.controller;

import com.bupt.charger.common.ApiResp;
import com.bupt.charger.request.AdminLoginRequest;
import com.bupt.charger.request.LoginRequest;
import com.bupt.charger.response.AdminLoginResponse;
import com.bupt.charger.response.UserLoginResponse;
import com.bupt.charger.service.AdminService;
import com.bupt.charger.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

/**
 * @author ll （ created: 2023-05-26 19:41 )
 */
@RestController
@Tag(name = "登录")
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "用户登录")
    public ResponseEntity<Object> userLogin(@RequestBody LoginRequest loginRequest) {
        try {
            UserLoginResponse loginResponse = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok().body(new ApiResp(0, "请求成功", loginResponse));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

}
