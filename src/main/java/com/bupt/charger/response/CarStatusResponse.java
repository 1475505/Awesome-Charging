package com.bupt.charger.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ll （ created: 2023-05-27 20:57 )
 */
@Data
public class CarStatusResponse extends Resp {
    @JsonProperty("car_number_before_position")
    private int carNumberBeforePosition = -1;

    @JsonProperty("car_state")
    private String carState;

    @JsonProperty("queue_num")
    private String queueNum;

    @JsonProperty("request_time")
    private long requestTime = -1;
}
