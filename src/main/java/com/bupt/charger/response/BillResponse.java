package com.bupt.charger.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ll （ created: 2023-05-27 21:17 )
 */
@Data
public class BillResponse extends Resp {
    @JsonProperty("car_id")
    private String carId;

    @JsonProperty("date")
    private String date;

    @JsonProperty("bill_id")
    private long billId;

    @JsonProperty("pile_id")
    private String pileId;

    @JsonProperty("charge_amount")
    private double chargeAmount;

    @JsonProperty("charge_duration")
    private int chargeDuration;

    @JsonProperty("start_time")
    private long startTime;

    @JsonProperty("end_time")
    private long endTime;

    @JsonProperty("charge_fee")
    private double chargeFee;

    @JsonProperty("service_fee")
    private double serviceFee;

    @JsonProperty("subtotal_fee")
    private double subtotalFee;
}
