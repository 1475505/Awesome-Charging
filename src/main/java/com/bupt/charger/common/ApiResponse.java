package com.bupt.charger.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author ll （ created: 2023-05-26 19:40 )
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private int code;
    private String msg;

    public ApiResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Object data;
}

