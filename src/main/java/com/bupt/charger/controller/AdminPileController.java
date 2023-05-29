package com.bupt.charger.controller;


import com.bupt.charger.common.ApiResp;
import com.bupt.charger.request.LogoutRequest;
import com.bupt.charger.request.StartPileRequest;
import com.bupt.charger.response.AdminLoginResponse;
import com.bupt.charger.response.AdminPileResponse;
import com.bupt.charger.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wxl,wyf （ created: 2023-05-29 19 :42 )
 */


@RestController
@RequestMapping("/admin/startCharger")
public class AdminPileController {

    @Autowired
    private AdminService adminService;

    @PostMapping
    @Operation(summary = "管理员启动充电桩")
    public ResponseEntity<Object> startPile(@RequestBody StartPileRequest request)  {
        try {
            adminService.startPile(request);
            return ResponseEntity.ok().body(new ApiResp(0, "请求成功"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }
}
