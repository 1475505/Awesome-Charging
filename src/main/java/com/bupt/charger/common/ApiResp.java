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
public class ApiResp {
    private int code = 0;
    private String msg = "请求成功";

    public ApiResp(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResp(Object obj) {
        this.data = obj;
    }

    private Object data;
}

