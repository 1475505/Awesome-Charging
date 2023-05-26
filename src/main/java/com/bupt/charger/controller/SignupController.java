package com.bupt.charger.controller;

import com.bupt.charger.common.ApiResponse;
import com.bupt.charger.exception.RegistrationException;
import com.bupt.charger.request.UserRegistrationRequest;
import com.bupt.charger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ll （ created: 2023-05-26 19:37 )
 */
@RestController
@RequestMapping("/signup")
public class SignupController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Object> signup(@RequestBody UserRegistrationRequest registrationRequest) {
        try {
            userService.registerUser(registrationRequest);
            return ResponseEntity.ok().body(new ApiResponse(0, "请求成功"));
        } catch (RegistrationException e) {
            return ResponseEntity.ok().body(new ApiResponse(1, e.getMessage()));
        }
    }
}
