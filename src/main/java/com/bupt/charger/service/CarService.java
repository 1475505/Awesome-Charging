package com.bupt.charger.service;

import com.bupt.charger.entity.Car;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.entity.ChargingQueue;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.CarRepository;
import com.bupt.charger.repository.ChargeReqRepository;
import com.bupt.charger.repository.ChargingQueueRepository;
import com.bupt.charger.response.CarChargingResponse;
import com.bupt.charger.response.CarStatusResponse;
import com.bupt.charger.response.Resp;
import com.bupt.charger.util.Estimator;
import com.bupt.charger.util.FormatUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ll （ created: 2023-05-27 20:44 )
 */
@Service
@Log
public class CarService {

    @Autowired
    CarRepository carRepository;

    @Autowired
    ChargingQueueRepository chargingQueueRepository;

    @Autowired
    ChargeReqRepository chargeReqRepository;

    public CarStatusResponse getCarStatus(String carId) {
        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            throw new ApiException("车牌不存在");
        }

        var queueId = car.getQueueNo();
        CarStatusResponse resp = new CarStatusResponse();
        resp.setQueueNum(queueId);
        ChargingQueue chargingQueue = chargingQueueRepository.findByQueueId(queueId);
        if (chargingQueue != null) {
            resp.setCarNumberBeforePosition(chargingQueue.getQueueIdx(carId));
        }
        resp.setCarState(car.getStatus().toString());

        Estimator estimator = new Estimator();
        if (car.getStatus() == Car.Status.charging) {
            resp.setRequestTime(estimator.estimateCarLeftChargeTime(carId).getSeconds());
        } else {
            resp.setRequestTime(estimator.estimateQueueWaitingTime(carId).getSeconds());
        }


        return resp;

    }

    public CarChargingResponse getCarCharging(String carId) {
        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            throw new ApiException("车牌不存在");
        }

        CarChargingResponse resp = new CarChargingResponse();
        long reqId = car.getHandingReqId();
        var requestOptional = chargeReqRepository.findById(reqId);

        if  (requestOptional.isEmpty()) {
            throw new ApiException("车辆没有正在进行的充电请求");
        }

        var request = requestOptional.get();
        resp.setOrderId(reqId);
        resp.setCreateTime(FormatUtils.LocalDateTime2Long(request.getCreatedAt()));
        resp.setPileId(car.getPileId());


        //TODO: 更新实时充电量和时间
        resp.setAmount(request.getDoneAmount());
        resp.setChargingStartTime(FormatUtils.LocalDateTime2Long(request.getStartChargingTime()));
        // 在充电的话，endTime算当前时间。
        resp.setChargingEndTime(System.currentTimeMillis());
        resp.setChargingLastTime(resp.getChargingEndTime() - resp.getChargingStartTime());

        //TODO: 算钱


        return resp;
    }


}
