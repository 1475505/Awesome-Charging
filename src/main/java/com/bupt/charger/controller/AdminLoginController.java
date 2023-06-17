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
 * @author wxl,wyf （ created: 2023-05-29 15:31 )
 */
@RestController
@Tag(name = "登录")
@RequestMapping("/admin/login")
public class AdminLoginController {

    @Autowired
    private AdminService adminService;

    @PostMapping
    @Operation(summary = "管理员登录")
    public ResponseEntity<Object> adminLogin(@RequestBody AdminLoginRequest adminLoginRequest) {
        try {
            AdminLoginResponse adminLoginResponse = adminService.login(adminLoginRequest.getAdminName(), adminLoginRequest.getPassword());
            return ResponseEntity.ok().body(new ApiResp(0, "请求成功", adminLoginResponse));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

}
