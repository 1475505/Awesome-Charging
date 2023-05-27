package com.bupt.charger.service;

import com.bupt.charger.entity.Car;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.entity.User;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.CarRepository;
import com.bupt.charger.repository.ChargeReqRepository;
import com.bupt.charger.request.ChargeReqRequest;
import com.bupt.charger.request.ModifyChargeAmountRequest;
import com.bupt.charger.request.ModifyChargeModeRequest;
import com.bupt.charger.response.ChargeReqResponse;
import com.bupt.charger.response.UserLoginResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

/**
 * @author ll （ created: 2023-05-27 18:04 )
 */
@Service
@Log
public class ChargeService {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ChargeReqRepository chargeReqRepository;


    public ChargeReqResponse chargeRequest(ChargeReqRequest chargeReqRequest) {
        log.info("User request charge: " + chargeReqRequest);
        var carId = chargeReqRequest.getCarId();
        ChargeRequest chargeRequest = new ChargeRequest();
        if (!carRepository.existsByCarId(carId)) {
            throw new ApiException("车牌不存在");
        } else
            chargeRequest.setCarId(carId);

        Car car = carRepository.findByCarId(carId);

        if (car.isCharging()) {
            throw new ApiException("车辆已经在充电啦");
        }

        chargeRequest.setRequestAmount(chargeReqRequest.getRequestAmount());
        chargeRequest.setRequestMode(chargeReqRequest.getRequestMode());

        chargeReqRepository.save(chargeRequest);

        //TODO

        ChargeReqResponse response = new ChargeReqResponse();
        response.setCarPosition(car.getArea().toString());
        response.setCarState(car.getStatus().toString());
        response.setQueue(car.getQueueNo());

        return response;
    }

    public void ModifyRequestAmount(ModifyChargeAmountRequest request) {
        var carId = request.getCarId();
        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            throw new ApiException("车牌不存在");
        }

        if (car.getStatus() == Car.Status.charging) {
            throw new ApiException("车辆已在充电，不可修改");
        }

        ChargeRequest chargeRequest = chargeReqRepository.getLatestUnDoneRequests(carId);
        if (chargeRequest == null) {
            throw new ApiException("没有符合条件的请求，可能是没有未完成的充电请求");
        }

        chargeRequest.setRequestAmount(request.getRequestAmount());
        chargeReqRepository.save(chargeRequest);

        //TODO
    }

    public void ModifyRequestMode(ModifyChargeModeRequest request) {
        var carId = request.getCarId();
        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            throw new ApiException("车牌不存在");
        }

        if (car.getStatus() == Car.Status.charging) {
            throw new ApiException("车辆已在充电，不可修改");
        }

        ChargeRequest chargeRequest = chargeReqRepository.getLatestUnDoneRequests(carId);

        if (chargeRequest == null) {
            throw new ApiException("没有符合条件的请求，可能是没有未完成的充电请求");
        }

        chargeRequest.setRequestMode(request.getRequestMode());
        chargeReqRepository.save(chargeRequest);

        //TODO
    }
}
