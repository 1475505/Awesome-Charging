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
import com.bupt.charger.util.Calculator;
import com.bupt.charger.util.Estimator;
import com.bupt.charger.util.FormatUtils;
import lombok.extern.java.Log;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.lang.Math.min;

/**
 * @author ll （ created: 2023-05-27 20:44 )
 */
@Service
@Log
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ChargingQueueRepository chargingQueueRepository;

    @Autowired
    private ChargeReqRepository chargeReqRepository;

    // -1 means error
    public double updateDoneAmount(String carId) {
        Car car = carRepository.findByCarId(carId);
        var reqId = car.getHandingReqId();
        var requestOptional = chargeReqRepository.findById(reqId);
        if (requestOptional.isEmpty()) {
            return -1;
        }
        ChargeRequest request = requestOptional.get();

        Calculator calculator = new Calculator();
        LocalDateTime now = FormatUtils.getNowLocalDateTime();
        double amount = calculator.getChargeAmount(request.getStartChargingTime(), now, request.getRequestMode());
        amount = min(amount, request.getRequestAmount());
        request.setDoneAmount(amount);
        chargeReqRepository.save(request);

        //TODO: 如果发现大于等于requestAmount，通知前端

        return amount;
    }

    //6.获取车队列的状态。
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
        updateDoneAmount(carId);
        if (car.getStatus() == Car.Status.charging) {
            resp.setRequestTime(estimator.estimateCarLeftChargeTime(carId).getSeconds());
        } else {
            resp.setRequestTime(estimator.estimateQueueWaitingTime(carId).getSeconds());
        }

        return resp;

    }

    //8. checkStatus，获取充电中的车的状态。
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

        double amount = updateDoneAmount(carId);
        if (amount < 0) {
            throw new ApiException("充电请求异常，请联系客服");
        }

        resp.setAmount(amount);
        resp.setChargingStartTime(FormatUtils.LocalDateTime2Long(request.getStartChargingTime()));

        Estimator estimator = new Estimator();
        Duration leftTime = estimator.estimateCarLeftChargeTime(carId);

        resp.setChargingLastTime(Duration.between(request.getStartChargingTime(), FormatUtils.getNowLocalDateTime()).getSeconds());
        resp.setChargingEndTime(FormatUtils.LocalDateTime2Long(FormatUtils.getNowLocalDateTime().plus(leftTime)));

        return resp;
    }
}
