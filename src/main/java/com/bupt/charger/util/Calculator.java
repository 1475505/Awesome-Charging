package com.bupt.charger.util;

import com.bupt.charger.config.PileConfig;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.entity.Pile;
import com.bupt.charger.repository.PilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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

         return seconds / 3600.0 * power ;
    }

    public double getChargeFee(LocalDateTime startTime, LocalDateTime endTime, String pileNo, double amount) {
        Pile pile = pilesRepository.findByPileId(pileNo);
        // 接下来是判断time在哪个时间段，切割计费。注意以pile的功率为准。
        // TODO
        return 0;
    }
}
