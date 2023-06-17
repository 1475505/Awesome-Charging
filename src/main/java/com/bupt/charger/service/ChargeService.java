package com.bupt.charger.service;

import com.bupt.charger.config.AppConfig;
import com.bupt.charger.entity.*;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.*;
import com.bupt.charger.request.ChargeReqRequest;
import com.bupt.charger.request.ModifyChargeAmountRequest;
import com.bupt.charger.request.ModifyChargeModeRequest;
import com.bupt.charger.response.ChargeReqResponse;
import com.bupt.charger.util.Calculator;
import com.bupt.charger.util.Estimator;
import com.bupt.charger.util.FormatUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

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
            throw new ApiException("车辆已经在充电进程啦");
        }

        // 检查等候区是否已经爆满
        if (isWaitingAreaFull()) {
            throw new ApiException("等候区已满");
        }

        chargeRequest.setRequestAmount(chargeReqRequest.getRequestAmount());
        chargeRequest.setRequestMode(chargeReqRequest.getRequestMode());
        chargeRequest.setStatus(ChargeRequest.Status.DOING);

        chargeReqRepository.save(chargeRequest);

        car.setHandingReqId(chargeRequest.getId());
        carRepository.save(car);

        // 有空位则移到等候区
        // 加入等待队列
        scheduleService.moveToWaitingQueue(car);

        ChargeReqResponse response = new ChargeReqResponse();
        response.setCarPosition(car.getArea().toString());
        response.setCarState(car.getStatus().toString());
        response.setQueue(car.getQueueNo());

        // 预计等待时间
        if (car.getArea() == Car.Area.WAITING) {
            var queueWaitingTime = estimator.estimateQueueWaitingTime(carId);
            response.setRequestTime(queueWaitingTime.getSeconds());
        } else if (car.getArea() == Car.Area.CHARGING) {
            Pile pile = pilesRepository.findByPile(car.getPileId());
            int idx = pile.getQueueIdx(carId);
            if (idx == 1) {
                taskService.scheduleTask(carId, Duration.ZERO, "可以开始充电啦");
            }
            var chargingWaitingTime = estimator.estimateChargingQueueWaitingTime(carId);
            response.setRequestTime(chargingWaitingTime.getSeconds());
        }
        return response;
    }

    // 检查等候区是否已经爆满
    private boolean isWaitingAreaFull() {
        // TODO: 获取等候区最大数目，读取配置
        int maxWaitingNum = 6;

        ChargingQueue f = chargingQueueRepository.findByQueueId("F");
        ChargingQueue t = chargingQueueRepository.findByQueueId("T");
        int nowWaitingNum = f.getWaitingCarCnt() + t.getWaitingCarCnt();
        System.out.println("nowWaitingNum: " + nowWaitingNum);
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

        // 将该车从等候区中删除,然后添加到更改后模式的等候区中
        scheduleService.removeFromWaitingQueue(car.getCarId(), oldMode);
        String carQueueNo = scheduleService.moveToWaitingQueue(car);
    }

    public void startCharging(String carId) {
        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            throw new ApiException("车辆不存在");
        }

        if (!car.inChargingProcess()) {
            throw new ApiException("车辆未申请充电");
        }

        // 检测车辆是对应队列的第一个才可以继续
        if (car.canCharging() &&
                !car.getCarId().equals(pilesRepository.findByPile(car.getPileId()).getQList().get(0))) {
            throw new ApiException("车辆目前的状态不可以开始充电哦");
        }
        var requestOptionalal = chargeReqRepository.findById(car.getHandingReqId());
        if (requestOptionalal.isEmpty()) {
            throw new ApiException("请先提交充电请求并等待处理");
        }

        ChargeRequest request = requestOptionalal.get();
        // 包含对car、chargeRequest、pile、queue等的调度和更新。
        // 1. 首先获取分配到的充电桩，basicSchedule函数应该是帮我们更新好了调度结果，并且已在充电桩入队。
        String targetPile = car.getPileId();
        Pile pile = pilesRepository.findByPile(targetPile);
        if (pile == null) {
            throw new ApiException("车辆没有被分配到的充电桩");
        }
//        if (!pile.getQList().get(0).equals(carId)) {
//            throw new ApiException("当前充电桩没轮到此车充电");
//        }
        pile.setStatus(Pile.Status.CHARGING);
        pilesRepository.save(pile);
        request.setStartChargingTime(FormatUtils.getNowLocalDateTime());

        // 2. 更新车辆状态
        car.setStatus(Car.Status.charging);


        //3. 通知结束充电。这个是定时提醒前端的结束
        taskService.scheduleTask(carId, estimator.estimateCarChargeTime(carId).dividedBy(appConfig.TIME_SCALE_FACTOR), "你的电电应该充满啦~");

        carRepository.save(car);
    }

    // 这个时用户调用的停止充电请求，和故障管理员调用的不一样
    public void stopCharging(String carId) {
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

        ChargeRequest request = requestOptionalal.get();

        // 在等候区取消充电
        if (car.getArea() == Car.Area.WAITING) {
            // 从等候区相关队列中移除该车辆
            scheduleService.removeFromWaitingQueue(car.getCarId(), request.getRequestMode());
            car.releaseChargingProcess();
            request.setStatus(ChargeRequest.Status.CANCELED);
            chargeReqRepository.save(request);
            carRepository.save(car);
            return;
        }

        String pileNo = car.getPileId();
        Pile pile = pilesRepository.findByPile(pileNo);

        // 车辆在充电区等候时取消充电，加宇移除相关队列
        if (car.getArea() == Car.Area.CHARGING && car.getStatus() != Car.Status.charging) {
            request.setStatus(ChargeRequest.Status.CANCELED);
            chargeReqRepository.save(request);
            // 移除从充电区队列删除
            if (!pile.removeCar(carId)) {
                log.info("出现删除故障");
                return;
            }
            car.releaseChargingProcess();
            carRepository.save(car);
            // 保存充电桩
            pilesRepository.save(pile);
            // 叫号队列中下一辆车开始充电
            scheduleService.remindCarStartCharge(pile.getPileId());

            // 结束充电后调用调度函数，将等候区的车辆移到充电区
            scheduleService.moveToChargingQueue();
            return;
        }

        //计算实际充电量
        LocalDateTime startTime = request.getStartChargingTime();
        double amount = carService.updateDoneAmount(carId);

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
        billRepository.save(bill);

        request.setEndChargingTime(endTime);
        request.setStatus(ChargeRequest.Status.DONE);
        request.setBillId(bill.getId());
        chargeReqRepository.save(request);

        // 释放车的充电状态
        car.releaseChargingProcess();
        carRepository.save(car);

        // 释放充电桩的状态
        pile.setStatus(Pile.Status.FREE);
        pile.consumeWaitingCar();
        pilesRepository.save(pile);

        // 叫号队列中下一辆车开始充电
        scheduleService.remindCarStartCharge(pile.getPileId());

        // 结束充电后调用调度函数，将等候区的车辆移到充电区
        scheduleService.moveToChargingQueue();
    }


}
