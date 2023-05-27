package com.bupt.charger.controller;

import com.bupt.charger.common.ApiResp;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.request.ChargeReqRequest;
import com.bupt.charger.request.ModifyChargeAmountRequest;
import com.bupt.charger.request.ModifyChargeModeRequest;
import com.bupt.charger.response.Resp;
import com.bupt.charger.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ll （ created: 2023-05-27 18:02 )
 */
@RestController
@RequestMapping("/user")
public class UserChargeController {

    @Autowired
    private ChargeService chargeService;

    @PostMapping("/request")
    public ResponseEntity<Object> reqCharge(@RequestBody ChargeReqRequest chargeReqRequest) {
        try {
            Resp resp = chargeService.chargeRequest(chargeReqRequest);
            return ResponseEntity.ok().body(new ApiResp(resp));
        } catch (ApiException e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

    @PostMapping("modifyAmount")
    public ResponseEntity<Object> modifyAmount(@RequestBody ModifyChargeAmountRequest request) {
        try {
            chargeService.ModifyRequestAmount(request);
            return ResponseEntity.ok().body(new ApiResp(0, "请求成功"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

    @PostMapping("chMode")
    public ResponseEntity<Object> modifyMode(@RequestBody ModifyChargeModeRequest request) {
        try {
            chargeService.ModifyRequestMode(request);
            return ResponseEntity.ok().body(new ApiResp(0, "请求成功"));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

}