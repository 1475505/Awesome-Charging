package com.bupt.charger.request;

import lombok.Data;

/**
 * @author ll （ created: 2023-05-26 19:37 )
 */
@Data
public class UserRegistrationRequest {
    private String username;
    private String password;
    private String carId;
}
