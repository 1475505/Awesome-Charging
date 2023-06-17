package com.bupt.charger.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CarResponse extends Resp {
    @JsonProperty("car_capacity")
    private int car_capacity = 22;

    @JsonProperty("car_id")
    private String car_id;

    @JsonProperty("request_amount")
    private double request_amount;

    @JsonProperty("wait_time")
    private int wait_time=0;


    public CarResponse(int car_capacity, String car_id, double request_amount, int wait_time) {
        this.car_capacity = car_capacity;
        this.car_id = car_id;
        this.request_amount = request_amount;
        this.wait_time = wait_time;
    }
}
