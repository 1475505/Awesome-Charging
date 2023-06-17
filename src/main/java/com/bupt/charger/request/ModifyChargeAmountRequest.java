package com.bupt.charger.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author ll （ created: 2023-05-27 17:59 )
 */
@Data
@ToString
public class ModifyChargeAmountRequest {
    @JsonProperty("car_id")
    private String carId;

    @JsonProperty("request_amount")
    private double requestAmount;
}
