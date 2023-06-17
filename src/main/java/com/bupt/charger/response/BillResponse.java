package com.bupt.charger.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ll （ created: 2023-05-27 21:17 )
 */
@Data
public class BillResponse extends Resp {
    @JsonProperty("car_id")
    private String carId;

    @JsonProperty("date")
    private String date;

    @JsonProperty("order_id")
    private long orderId;

    @JsonProperty("bill_id")
    public List<Long> billId = new ArrayList<>();

    @JsonProperty("pile_id")
    public List<String> pileId = new ArrayList<>();

    @JsonProperty("charge_amount")
    private double chargeAmount;

    @JsonProperty("charge_duration")
    public Long chargeDuration;

    @JsonProperty("start_time")
    public Long startTime;

    @JsonProperty("end_time")
    public Long endTime;

    @JsonProperty("total_charge_fee")
    private double chargeFee;

    @JsonProperty("total_service_fee")
    private double serviceFee;

    @JsonProperty("total_fee")
    private double totalFee;
}
