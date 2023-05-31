package com.bupt.charger.response;

import com.bupt.charger.entity.Car;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wyf （ created: 2023-05-30 20:52 )
 */
@Data
public class CheckChargerQueueResponse extends Resp {
    @JsonProperty("cars")
    List<Car> cars = new ArrayList<>();
}
