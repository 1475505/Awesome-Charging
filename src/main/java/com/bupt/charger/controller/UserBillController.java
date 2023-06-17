package com.bupt.charger.controller;

import com.bupt.charger.common.ApiResp;
import com.bupt.charger.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ll （ created: 2023-05-27 20:43 )
 */
@RestController
@RequestMapping("/user")
public class UserBillController {

    @Autowired
    BillService bilService;


    @GetMapping("/getBill")
    public ResponseEntity<?> getBill(@RequestParam("bill_id") long billId) {
        try {
            var resp = bilService.getBill(billId);
            return ResponseEntity.ok().body(new ApiResp(resp));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

    @GetMapping("/checkBill")
    public ResponseEntity<?> checkBill(@RequestParam("car_id") String carId) {
        try {
            var resp = bilService.checkBill(carId);
            return ResponseEntity.ok().body(new ApiResp(resp));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

    @GetMapping("/exportAllBill")
    public ResponseEntity<?> exportAllBill() {
        try {
            bilService.exportAll(); // 这个函数没有返回值
            return ResponseEntity.ok().body(new ApiResp());
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }
}
