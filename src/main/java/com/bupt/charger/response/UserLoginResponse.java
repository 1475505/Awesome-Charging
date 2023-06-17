package com.bupt.charger.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
/**
 * @author ll （ created: 2023-05-26 19:43 )
 */
@Data
public class UserLoginResponse extends Resp {
    private String username;
    @JsonProperty("car_id")
    private String carId;
}