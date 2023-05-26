package com.bupt.charger.controller;

import com.bupt.charger.common.ApiResponse;
import com.bupt.charger.request.LoginRequest;
import com.bupt.charger.response.UserLoginResponse;
import com.bupt.charger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

/**
 * @author ll （ created: 2023-05-26 19:41 )
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserLoginResponse loginResponse = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok().body(new ApiResponse(0, "请求成功", loginResponse));
        } catch (LoginException e) {
            return ResponseEntity.ok().body(new ApiResponse(1, e.getMessage()));
        }
    }

}
