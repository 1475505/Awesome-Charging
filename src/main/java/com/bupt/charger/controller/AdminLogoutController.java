package com.bupt.charger.controller;

import com.bupt.charger.common.ApiResp;
import com.bupt.charger.request.LoginRequest;
import com.bupt.charger.request.LogoutRequest;
import com.bupt.charger.response.UserLoginResponse;
import com.bupt.charger.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author wxl,wyf （ created: 2023-05-29 15:49 )
 */
@RestController
@RequestMapping("/admin/logout")
public class AdminLogoutController {

    @PostMapping
    public ResponseEntity<Object> logout(@RequestBody LogoutRequest request)  {
        try {
            return ResponseEntity.ok().body(new ApiResp(0, "请求成功"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

}