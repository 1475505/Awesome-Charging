package com.bupt.charger.response;

import com.bupt.charger.entity.Pile;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author wyf （ created: 2023-05-30 19:31 )
 * 说明：参数从上到下依次是 工作状态（”故障(-1)“，”未启动“，”启动且空闲“，”正在给车充电“）、
 * 一共充电次数、总的运行时间、总的充电度数。
 * 这个总计使用的是从系统创建以来开始计算的，也就是保存所有历史数量。
 */
@Data
public class CheckChargerResponse {
    @JsonProperty("working_state")
    private int workingState;

    @JsonProperty("total_charge_num")
    private long totalChargeNum;

    @JsonProperty("total_charge_time")
    private long totalChargeTime;

    @JsonProperty("total_capacity")
    private long totalCapacity;


}