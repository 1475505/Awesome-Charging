package com.bupt.charger.controller;

import com.bupt.charger.common.ApiResp;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.service.CarService;
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
public class UserCarController {

    @Autowired
    CarService carService;

    @GetMapping("/checkWaitQueue")
    public ResponseEntity<?> checkWaitQueue(@RequestParam("car_id") String carId) {
        try {
            var resp = carService.getCarStatus(carId);
            return ResponseEntity.ok().body(new ApiResp(resp));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

    @GetMapping("/checkStatus")
    public ResponseEntity<?> checkStatus(@RequestParam("car_id") String carId) {
        try {
            var resp = carService.getCarCharging(carId);
            return ResponseEntity.ok().body(new ApiResp(resp));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }

}
