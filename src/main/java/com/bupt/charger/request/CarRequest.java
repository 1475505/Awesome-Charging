package com.bupt.charger.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author ll （ created: 2023-05-26 23:44 )
 */
@Data
@ToString
public class CarRequest {
    @JsonProperty("car_id")
    private String carId;
}
