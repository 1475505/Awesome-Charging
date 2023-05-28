package com.bupt.charger.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ll （ created: 2023-05-28 10:32 )
 */
@Data
public class CarChargingResponse {
    @JsonProperty("order_id")
    private long orderId;

    @JsonProperty("create_time")
    private long createTime;

    @JsonProperty("pile_id")
    private long pileId;

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("charging_last_time")
    private long chargingLastTime;

    @JsonProperty("charging_start_time")
    private long chargingStartTime;

    @JsonProperty("charging_end_time")
    private long chargingEndTime;

    @JsonProperty("charging_fees")
    private double chargingFees;

    @JsonProperty("serve_fees")
    private double serveFees;

    @JsonProperty("total")
    private double total;
}
