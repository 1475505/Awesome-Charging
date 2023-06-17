package com.bupt.charger.service;

import com.bupt.charger.entity.Car;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.entity.ChargingQueue;
import com.bupt.charger.entity.Pile;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.CarRepository;
import com.bupt.charger.repository.ChargeReqRepository;
import com.bupt.charger.repository.ChargingQueueRepository;
import com.bupt.charger.repository.PilesRepository;
import com.bupt.charger.response.CarChargingResponse;
import com.bupt.charger.response.CarStatusResponse;
import com.bupt.charger.util.Calculator;
import com.bupt.charger.util.Estimator;
import com.bupt.charger.util.FormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.lang.Math.min;

/**
 * @author ll （ created: 2023-05-27 20:44 )
 */
@Service
@Slf4j
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ChargingQueueRepository chargingQueueRepository;

    @Autowired
    private ChargeReqRepository chargeReqRepository;

    @Autowired
    private Estimator estimator;

    @Autowired
    private Calculator calculator;

    @Autowired
    private TaskService taskService;

    @Autowired
    private PilesRepository pilesRepository;


    // -1 means error
    public double updateDoneAmount(String carId) {
        Car car = carRepository.findByCarId(carId);
        var reqId = car.getHandingReqId();
        var requestOptional = chargeReqRepository.findById(reqId);
        if (requestOptional.isEmpty()) {
            return -1;
        }
        ChargeRequest request = requestOptional.get();

        LocalDateTime now = FormatUtils.getNowLocalDateTime();
        double amount = calculator.getChargeAmount(request.getStartChargingTime(), now, request.getRequestMode());
        //如果发现大于等于requestAmount，通知前端。毕竟已经注册过前端了，可能确实会重复通知。
        if (amount > request.getDoneAmount()) {
            taskService.scheduleTask(carId, Duration.ofSeconds(0), "你的电电似乎充满了~");
        }
        amount = min(amount, request.getRequestAmount());
        request.setDoneAmount(amount);
        chargeReqRepository.save(request);

        return amount;
    }

    //6.获取车队列的状态。
    public CarStatusResponse getCarStatus(String carId) {
        Car car = carRepository.findByCarId(carId);

        if (car == null) {
            throw new ApiException("车牌不存在");
        }

        CarStatusResponse resp = new CarStatusResponse();
        resp.setCarState(car.getStatus().toString());

        if (car.getArea() == Car.Area.WAITING) {
            var queueId = car.getQueueNo();
            resp.setQueueNum(queueId);
            ChargingQueue chargingQueue = chargingQueueRepository.findByQueueId(queueId);
            if (chargingQueue != null) {
                resp.setCarNumberBeforePosition(chargingQueue.getQueueIdx(carId));
            }
        } else if (car.getStatus() == Car.Status.charging) {
            updateDoneAmount(carId);
            resp.setRequestTime(estimator.estimateCarLeftChargeTime(carId).getSeconds());
            resp.setCarNumberBeforePosition(0);
            resp.setQueueNum(car.getPileId()); //充电桩队列名是不是和充电桩名字一样
        } else if (car.getStatus() == Car.Status.waiting && car.getArea() == Car.Area.CHARGING) {
            Pile pile = pilesRepository.findByPile(car.getPileId());
            if (pile == null) {
                throw new ApiException("车辆在充电区等待但尚未被分配充电桩队列，请联系客服");
            }
            int idx = pile.getQueueIdx(carId);
            resp.setCarNumberBeforePosition(idx - 1);
            if (pile.getQueueIdx(carId) == 1) {
                taskService.scheduleTask(carId, Duration.ZERO, "车辆可以开始充电啦~");
            } else if (pile.getQCnt() > 1) {
                updateDoneAmount(pile.getQList().get(0));
            }
            resp.setQueueNum(car.getPileId());
            resp.setRequestTime(estimator.estimateChargingQueueWaitingTime(carId).getSeconds());
        }
        return resp;
    }

    //8. checkStatus，获取充电中的车的请求状态。
    public CarChargingResponse getCarCharging(String carId) {
        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            throw new ApiException("车牌不存在");
        }

        if (car.getStatus() != Car.Status.charging) {
            throw new ApiException("车辆不在充电中");
        }

        CarChargingResponse resp = new CarChargingResponse();
        long reqId = car.getHandingReqId();
        var requestOptional = chargeReqRepository.findById(reqId);

        if (requestOptional.isEmpty()) {
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

        Duration leftTime = estimator.estimateCarLeftChargeTime(carId);

        resp.setChargingLastTime(Duration.between(request.getStartChargingTime(), FormatUtils.getNowLocalDateTime()).getSeconds());
        resp.setChargingEndTime(FormatUtils.LocalDateTime2Long(FormatUtils.getNowLocalDateTime().plus(leftTime)));

        return resp;
    }
}
