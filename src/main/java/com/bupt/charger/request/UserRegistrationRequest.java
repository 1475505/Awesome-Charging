package com.bupt.charger.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author ll （ created: 2023-05-26 19:37 )
 */
@Data
@ToString
public class UserRegistrationRequest {
    private String username;
    private String password;
    @JsonProperty("car_id")
    private String carId;
}
