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
    private Object data;

    public ApiResponse(int i, String message) {
        setCode(i);
        setMsg(message);
    }
}

