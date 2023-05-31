package com.bupt.charger.util;

import com.bupt.charger.config.PileConfig;
import com.bupt.charger.entity.Car;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.entity.ChargingQueue;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.CarRepository;
import com.bupt.charger.repository.ChargeReqRepository;
import com.bupt.charger.repository.ChargingQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author ll （ created: 2023-05-31 20:39 )
 */
@Component
public class Estimator {

    private final CarRepository carRepository;
    private final ChargingQueueRepository chargingQueueRepository;
    private final ChargeReqRepository chargeReqRepository;
    private final PileConfig pileConfig;

    @Autowired
    public Estimator(
            CarRepository carRepository,
            ChargingQueueRepository chargingQueueRepository,
            ChargeReqRepository chargeReqRepository,
            PileConfig pileConfig
    ) {
        this.carRepository = carRepository;
        this.chargingQueueRepository = chargingQueueRepository;
        this.chargeReqRepository = chargeReqRepository;
        this.pileConfig = pileConfig;
    }

    // 用来返回这辆车完整充电所需的时间，适合用于车没有在充电时进行估算
    public Duration estimateCarChargeTime(String carId) {
        ChargeRequest request = getHandingChargeRequest(carId);

        double amount = request.getRequestAmount();
        var mode = request.getRequestMode();
        double power = 0;
        if (mode == ChargeRequest.RequestMode.FAST) {
            power = pileConfig.FAST_POWER;
        } else {
            power = pileConfig.SLOW_POWER;
        }

        double hours = amount / power;
        long seconds = (long) (hours * 3600); // 将小时数转换为秒数
        return Duration.ofSeconds(seconds);
    }

    public Duration estimateCarLeftChargeTime(String carId) {
        ChargeRequest request = getHandingChargeRequest(carId);

        double amount = request.getRequestAmount() - request.getDoneAmount();
        var mode = request.getRequestMode();
        double power = 0;
        if (mode == ChargeRequest.RequestMode.FAST) {
            power = pileConfig.FAST_POWER;
        } else {
            power = pileConfig.SLOW_POWER;
        }

        double hours = amount / power;
        long seconds = (long) (hours * 3600); // 将小时数转换为秒数
        return Duration.ofSeconds(seconds);
    }

    private ChargeRequest getHandingChargeRequest(String carId) {
        Car car = carRepository.findByCarId(carId);
        long reqId = car.getHandingReqId();
        var chargeRequestOptional = chargeReqRepository.findById(reqId);
        if (chargeRequestOptional.isEmpty()) {
            return null;
        }

        ChargeRequest chargeRequest = chargeRequestOptional.get();
        return chargeRequest;
    }

    // 计算等候区的的预计等待时间
    public Duration estimateQueueWaitingTime(String carId) {
        Car car = carRepository.findByCarId(carId);
        ChargingQueue queue = chargingQueueRepository.findByQueueId(car.getQueueNo());
        if (queue == null) {
            throw new ApiException("出错啦，找客服");
        }

        Duration result = Duration.ofSeconds(0);

        // 其队列前面的车的充电时长，因为可能被调度到多个充电桩，需要除以吞吐量
        int throughput = 2; // TODO！！
        int idx = queue.getQueueIdx(carId);
        for (int i = 0; i < idx; i++) {
            result = result.plus(estimateCarChargeTime(queue.getWaitingCarsList().get(idx)).dividedBy(throughput));
        }

        // 还需要等多久才能进去充电
        // result = result.plus(estimateEnChargingQueueWaitingTime());

        return result;
    }

    //TODO：
    public Duration estimateEnChargingQueueWaitingTime() {
        return Duration.ofSeconds(0);
    }

}
