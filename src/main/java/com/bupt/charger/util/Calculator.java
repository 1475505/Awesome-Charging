package com.bupt.charger.util;

import com.bupt.charger.config.PileConfig;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.entity.Pile;
import com.bupt.charger.repository.PilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author ll （ created: 2023-05-31 21:48 )
 */
@Component
public class Calculator {
    @Autowired
    PileConfig pileConfig;

    @Autowired
    PilesRepository pilesRepository;

    public double getChargeAmount(LocalDateTime start, LocalDateTime end, ChargeRequest.RequestMode mode) {
        long seconds = Duration.between(start, end).getSeconds();
        double power = pileConfig.FAST_POWER;
        if (mode == ChargeRequest.RequestMode.SLOW) {
            power = pileConfig.SLOW_POWER;
        }

        return seconds / 3600.0 * power;
    }

    public double getChargeFee(LocalDateTime startTime, LocalDateTime endTime, String pileId, double amount) {
        Pile pile = pilesRepository.findByPileId(pileId);
        // 接下来是判断time在哪个时间段，切割计费。注意以pile的功率为准。
        // Attention: 根据amount算时长，endTime暂时理解成不需要
        // TODO:这里暂时没测试，不知道正确性如何
        //这里是每10秒钟消耗的度数
        double fastratio = 30 / 360, slowratio = 7 / 360;
        //谷时、峰时、常时、服务费
        double peak_price = pile.getPeakPrice(), usual_price = pile.getUsualPrice(), valley_price = pile.getValleyPrice();
        double serve_price = pile.getServePrice();

        var pileMode = pile.getMode();

        double feePerUnitTime = 1; // 单位时间费用（这里假定为1元每小时）
        double powerPerUnitTime;

        double power;
        if (pileMode == Pile.Mode.F)//这里是快充
        {
            powerPerUnitTime = fastratio;
        } else {
            powerPerUnitTime = slowratio;
        }

        // 获取起止时间之间相差的秒数
        long allSeconds = Duration.between(startTime, endTime).toSeconds();


        double totalFee = 0; // 总费用
        double unitTime = 10; // 单位时间（秒）
        double remainingAmount = amount;//充电剩余电量


        for (LocalDateTime time = startTime; time.isBefore(endTime); time = time.plusSeconds(10)) {
            //这里给费用赋值
            if ((time.getHour() >= 10 && time.getHour() < 15) ||
                    (time.getHour() >= 18 && time.getHour() < 21)) {//峰时
                feePerUnitTime = peak_price;
            } else if ((time.getHour() >= 7 && time.getHour() < 10) ||
                    (time.getHour() >= 15 && time.getHour() < 18) ||
                    (time.getHour() >= 18 && time.getHour() < 21)) {//平时
                feePerUnitTime = usual_price;
            } else if ((time.getHour() == 23) ||
                    (time.getHour() >= 0 && time.getHour() < 7)) {//谷时
                feePerUnitTime = valley_price;
            }
            //所有单位费用均加上服务费
            feePerUnitTime += serve_price;

            remainingAmount = remainingAmount - powerPerUnitTime; // 剩余电量
            if (remainingAmount >= 0) {
                // 汽车未充满
                totalFee += feePerUnitTime / 360;
            }
        }

        return totalFee;
    }


}
