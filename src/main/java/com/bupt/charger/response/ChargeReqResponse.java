package com.bupt.charger.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ll （ created: 2023-05-26 19:43 )
 */
@Data
public class ChargeReqResponse extends Resp {
    @JsonProperty("car_position")
    private String carPosition;
    @JsonProperty("car_state")
    private String carState;
    @JsonProperty("queue_num")
    private String queue;
    @JsonProperty("request_time")
    private int requestTime = -1;
}