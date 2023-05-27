package com.bupt.charger.service;

import com.bupt.charger.entity.Car;
import com.bupt.charger.entity.ChargingQueue;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.CarRepository;
import com.bupt.charger.repository.ChargingQueueRepository;
import com.bupt.charger.response.CarStatusResponse;
import com.bupt.charger.response.Resp;
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

        //TODO: resp.setRequestTime();

        return resp;

    }
}
