package com.bupt.charger.controller;
import com.bupt.charger.common.ApiResp;
import com.bupt.charger.service.TimeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author nameless0078 （ created: 2023-06-03 16 :23 )
 */

@RestController
@RequestMapping("/timeNow")
public class GetTimeNowController {

    @Autowired
    private TimeService timeService;

    @GetMapping("")
    @Operation(summary = "获取时间")
    public ResponseEntity<Object> getTimeNow() {
        try {
            var response = timeService.getTimeNow();
            return ResponseEntity.ok().body(new ApiResp(response));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ApiResp(1, e.getMessage()));
        }
    }



}
