package com.bupt.charger.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ll （ created: 2023-05-27 21:32 )
 */
@Data
public class DateBillResponse {
    @JsonProperty("car_id")
    private String carId;

    @JsonProperty("date")
    private String date;

    @JsonProperty("order_id")
    private long orderId;

    @JsonProperty("bill_id")
    private String billId; // ","分割

    @JsonProperty("pile_id")
    private String pileId;

    @JsonProperty("charge_amount")
    private double chargeAmount;

    @JsonProperty("charge_duration")
    private long chargeDuration;

    //TODO： 都按天了，这是啥？忽略先
    @JsonProperty("start_time")
    private long startTime;

    @JsonProperty("end_time")
    private long endTime;

    @JsonProperty("total_charge_fee")
    private double chargeFee;

    @JsonProperty("total_service_fee")
    private double serviceFee;

    @JsonProperty("total_fee")
    private double totalFee;
}
