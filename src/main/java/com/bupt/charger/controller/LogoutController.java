package com.bupt.charger.controller;

import com.bupt.charger.common.ApiResp;
import com.bupt.charger.request.LoginRequest;
import com.bupt.charger.response.UserLoginResponse;
import com.bupt.charger.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ll （ created: 2023-05-26 19:41 )
 */
@RestController
@RequestMapping("/logout")
public class LogoutController {

    @PostMapping
    public ResponseEntity<Object> logout(@RequestParam("username") String username) {
        try {
            return ResponseEntity.ok().body(new ApiResp(0, "请求成功"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

}
