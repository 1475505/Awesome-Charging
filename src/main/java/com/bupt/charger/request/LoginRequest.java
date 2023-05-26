package com.bupt.charger.request;

import lombok.Data;
import lombok.ToString;

/**
 * @author ll （ created: 2023-05-26 23:44 )
 */
@Data
@ToString
public class LoginRequest {
    private String username;
    private String password;
}
