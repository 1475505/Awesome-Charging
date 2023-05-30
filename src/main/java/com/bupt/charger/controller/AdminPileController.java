package com.bupt.charger.controller;


import com.bupt.charger.common.ApiResp;
import com.bupt.charger.request.SetPileParametersRequest;
import com.bupt.charger.request.ShutDownPileRequest;
import com.bupt.charger.request.StartPileRequest;
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
@RequestMapping("/admin")
public class AdminPileController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/startCharger")
    @Operation(summary = "管理员启动充电桩")
    public ResponseEntity<Object> startPile(@RequestBody StartPileRequest request)  {
        try {
            adminService.startPile(request);
            return ResponseEntity.ok().body(new ApiResp(0, "请求成功"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

    @PostMapping("/offCharger")
    @Operation(summary = "管理员关闭充电桩")
    public ResponseEntity<Object> shutDownPile(@RequestBody ShutDownPileRequest request)  {
        try {
            adminService.shutDownPile(request);
            return ResponseEntity.ok().body(new ApiResp(0, "请求成功"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

    @PostMapping("/setParameters")
    @Operation(summary = "管理员修改充电桩状态")
    public ResponseEntity<Object> setPileParameters(@RequestBody SetPileParametersRequest request)  {
        try {
            adminService.setPileParameters(request);
            return ResponseEntity.ok().body(new ApiResp(0, "请求成功"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }
}
