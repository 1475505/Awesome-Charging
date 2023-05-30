package com.bupt.charger.request;


import lombok.Data;
import lombok.ToString;

/**
 * @author wyf（ created: 2023-05-14:01 14:02 )
 */
@Data
@ToString
public class AdminLoginRequest {
    private String adminName;
    private String password;
}
