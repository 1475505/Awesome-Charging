package com.bupt.charger.service;

import com.bupt.charger.entity.Car;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.entity.ChargingQueue;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.CarRepository;
import com.bupt.charger.repository.ChargeReqRepository;
import com.bupt.charger.repository.ChargingQueueRepository;
import com.bupt.charger.request.ChargeReqRequest;
import com.bupt.charger.request.ModifyChargeAmountRequest;
import com.bupt.charger.request.ModifyChargeModeRequest;
import com.bupt.charger.response.ChargeReqResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private ChargingQueueRepository chargingQueueRepository;

    private ScheduleService scheduleService;

    // 接收充电请求的所有处理
    public ChargeReqResponse chargeRequest(ChargeReqRequest chargeReqRequest) {
        log.info("User request charge: " + chargeReqRequest);
        var carId = chargeReqRequest.getCarId();
        ChargeRequest chargeRequest = new ChargeRequest();
        if (!carRepository.existsByCarId(carId)) {
            throw new ApiException("车牌不存在");
        } else
            chargeRequest.setCarId(carId);

        Car car = carRepository.findByCarId(carId);

        if (car.inChargingProcess()) {
            throw new ApiException("车辆已经在充电啦");
        }

        chargeRequest.setRequestAmount(chargeReqRequest.getRequestAmount());
        chargeRequest.setRequestMode(chargeReqRequest.getRequestMode());

        chargeReqRepository.save(chargeRequest);

        // 检查等候区是否已经爆满
        if (isWaitingAreaFull()) {
            throw new ApiException("等候区已满");
        }

        // 移到等候区
        // 设置车辆状态
        car.setStatus(Car.Status.waiting);
        car.setArea(Car.Area.WAITING);

        // 加入等待队列
        String carQueueNo = scheduleService.moveToWaitingQueue(car);
        car.setQueueNo(carQueueNo);

        carRepository.save(car);

        ChargeReqResponse response = new ChargeReqResponse();
        response.setCarPosition(car.getArea().toString());
        response.setCarState(car.getStatus().toString());
        response.setQueue(car.getQueueNo());

        return response;
    }

    // 检查等候区是否已经爆满
    private boolean isWaitingAreaFull() {
        // TODO: 获取等候区最大数目，读取配置
        int maxWaitingNum = 6;

        ChargingQueue f = chargingQueueRepository.findByQueueId("F");
        ChargingQueue t = chargingQueueRepository.findByQueueId("T");
        int nowWaitingNum = f.getWaitingCarCnt() + t.getWaitingCarCnt();
        return nowWaitingNum >= maxWaitingNum;
    }

    public void ModifyRequestAmount(ModifyChargeAmountRequest request) {
        var carId = request.getCarId();
        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            throw new ApiException("车牌不存在");
        }

        if (car.getArea() == Car.Area.CHARGING) {
            throw new ApiException("车辆已在充电区，不可修改");
        }

        ChargeRequest chargeRequest = chargeReqRepository.getLatestUnDoneRequests(carId);
        if (chargeRequest == null) {
            throw new ApiException("没有符合条件的请求，可能是没有未完成的充电请求");
        }

        chargeRequest.setRequestAmount(request.getRequestAmount());
        chargeReqRepository.save(chargeRequest);

    }

    public void ModifyRequestMode(ModifyChargeModeRequest request) {
        var carId = request.getCarId();
        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            throw new ApiException("车牌不存在");
        }

        if (car.getArea() == Car.Area.CHARGING) {
            throw new ApiException("车辆已在充电区，不可修改");
        }

        ChargeRequest chargeRequest = chargeReqRepository.getLatestUnDoneRequests(carId);

        if (chargeRequest == null) {
            throw new ApiException("没有符合条件的请求，可能是没有未完成的充电请求");
        }
        // 原来的充电模式
        ChargeRequest.RequestMode oldMode = chargeRequest.getRequestMode();
        chargeRequest.setRequestMode(request.getRequestMode());
        chargeReqRepository.save(chargeRequest);

        //    将该车从等候区中删除,然后添加到更改后模式的等候区中
        scheduleService.removeFromWaitingQueue(car.getCarId(), oldMode);
        String carQueueNo = scheduleService.moveToWaitingQueue(car);
        car.setQueueNo(carQueueNo);
        carRepository.save(car);
    }

    public void startCharging(String carId) {
        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            throw new ApiException("车辆不存在");
        }

        if (car.inChargingProcess()) {
            throw new ApiException("车辆目前的状态不可以开始充电哦");
        }

        var requestOptionalal = chargeReqRepository.findById(car.getHandingReqId());
        if (requestOptionalal.isEmpty()) {
            throw new ApiException("请先提交充电请求并等待处理");
        }

        ChargeRequest chargeRequest = requestOptionalal.get();

        // TODO，包含对car、chargeRequest、pile、queue等的调度和更新。


    }

    public void stopCharging(String carId) {
        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            throw new ApiException("车辆不存在");
        }

        if (car.getStatus() != Car.Status.charging) {
            throw new ApiException("车辆并未在充电");
        }

        var requestOptionalal = chargeReqRepository.findById(car.getHandingReqId());
        if (requestOptionalal.isEmpty()) {
            throw new ApiException("没有关联的充电请求，请联系客服");
        }

        ChargeRequest chargeRequest = requestOptionalal.get();

        // TODO，包含对car、chargeRequest、pile、queue等的更新，bill的生成，计费。


    }
}
