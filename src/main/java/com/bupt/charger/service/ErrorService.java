package com.bupt.charger.service;

import com.bupt.charger.config.AppConfig;
import com.bupt.charger.entity.Bill;
import com.bupt.charger.entity.Car;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.entity.Pile;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.*;
import com.bupt.charger.util.Calculator;
import com.bupt.charger.util.Estimator;
import com.bupt.charger.util.FormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.lang.Math.min;

@Service
public class ErrorService {
//    该类存放所有和故障服务相关的调度接口

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ChargeReqRepository chargeReqRepository;

    @Autowired
    private ChargingQueueRepository chargingQueueRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    PilesRepository pilesRepository;

    @Autowired
    BillRepository billRepository;

    @Autowired
    Calculator calculator;

    @Autowired
    Estimator estimator;

    @Autowired
    AppConfig appConfig;

    @Autowired
    TaskService taskService;

    @Autowired
    CarService carService;

    // 这个是故障机制中，故障充电桩车辆停止充电
    public void errorStopCharging(String carId) {
        //    停止充电，但是不能更改Car的进入等候区的状态

        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            throw new ApiException("车辆不存在");
        }

        if (!car.inChargingProcess()) {
            throw new ApiException("车辆并未在充电进程中");
        }

        var requestOptionalal = chargeReqRepository.findById(car.getHandingReqId());
        if (requestOptionalal.isEmpty()) {
            throw new ApiException("没有关联的充电请求，请联系客服");
        }

        LocalDateTime endTime = FormatUtils.getNowLocalDateTime();

        taskService.cancelTask(carId);

        // 标记充电请求为已完成
        ChargeRequest request = requestOptionalal.get();

        String pileNo = car.getPileId();
        Pile pile = pilesRepository.findByPileId(pileNo);

        request.setEndChargingTime(endTime);
        request.setStatus(ChargeRequest.Status.DONE);

        //计算实际充电量
        LocalDateTime startTime = request.getStartChargingTime();
        double amount = calculator.getChargeAmount(startTime, endTime, request.getRequestMode());
        amount = min(amount, request.getRequestAmount());
        request.setDoneAmount(amount);

        // 生成详单
        Bill bill = new Bill();
        bill.setCarId(carId);
        bill.setStartTime(startTime);
        bill.setEndTime(endTime);
        bill.setPileId(pileNo);
        bill.setChargeAmount(amount);
        double chargeFee = calculator.getChargeFee(startTime, endTime, pileNo, amount);
        bill.setChargeFee(chargeFee);
        bill.setServiceFee(amount * pile.getServePrice());
        // 保存
        billRepository.save(bill);
        chargeReqRepository.save(request);

        // 不需要设置车辆状态，因为之后进入调度队列自动设置

        // 需要为该车重建一个请求，传入没有充的电量
        ChargeRequest newChargeRequest = new ChargeRequest();
        newChargeRequest.setRequestAmount(request.getRequestAmount() - amount);
        newChargeRequest.setRequestMode(request.getRequestMode());
        newChargeRequest.setStatus(ChargeRequest.Status.DOING);
        newChargeRequest.setCarId(carId);
        //    保存
        chargeReqRepository.save(newChargeRequest);

        car.setHandingReqId(newChargeRequest.getId());
        carRepository.save(car);
    }

}
