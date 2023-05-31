package com.bupt.charger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ll （ created: 2023-05-27 20:08 )
 */
@Data
@Entity
@Table(name = "queues")
public class ChargingQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String queueId;

    private int capacity; //队列上限

    private String waitingCars; // 等待队列

    public List<String> getWaitingCarsList() {
        String input = waitingCars;
        List<String> resultList = new ArrayList<>();

        if (input != null && !input.isEmpty()) {
            String[] numberStrings = input.split(",");
            return Arrays.asList(numberStrings);
        }
        return resultList;
    }

    public int getWaitingCarCnt() {
        String input = waitingCars;
        List<String> resultList = new ArrayList<>();

        if (input != null && !input.isEmpty()) {
            String[] numberStrings = input.split(",");
            return numberStrings.length;
        }
        return 0;
    }

    /* 若添加成功，返回true，否则false  */
    public boolean addWaitingCar(String id) {
        if (getWaitingCarCnt() >= capacity) {
            return false;
        }
        if (waitingCars == null || waitingCars.isEmpty()) {
            waitingCars = String.valueOf(id);
        } else {
            waitingCars += "," + id;
        }
        return false;
    }

    public String consumeWaitingCar() {
        if (waitingCars == null || waitingCars.isEmpty()) {
            return null;
        }
        var consumeCar = getWaitingCarsList().get(0);
        if (getWaitingCarCnt() == 1) {
            waitingCars = "";
            return consumeCar;
        } else {
            // 移除首个元素
            int commaIndex = waitingCars.indexOf(",");
            if (commaIndex != -1) {
                waitingCars = waitingCars.substring(commaIndex + 1).trim();
            }
            return consumeCar;
        }
    }

    // return -1 means NOTFOUND
    public int getQueueIdx(String carId) {
        List<String> queueCars = getWaitingCarsList();
        for (int i = 0; i < queueCars.size(); i++) {
            if (queueCars.get(i).equals(carId)) {
                return i + 1;
            }
        }
        return -1;
    }

}
