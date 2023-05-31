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
    // 这个是模拟队列，
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 直接映射为具体的表名字，不加映射关系就是和数据库中列名一样
    private String queueId;

    // TODO: 为充电桩队列的最大长度，改为读取配置文件时设置这个值
    private int capacity = 20; //队列上限

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

    public String getTopCarId() {
        List<String> waitingCarsList = getWaitingCarsList();
        if (waitingCarsList.isEmpty()) {
            return null;
        }
        return waitingCarsList.get(0);
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

    // 这个是出队列
    public String consumeWaitingCar() {
        if (waitingCars == null || waitingCars.isEmpty()) {
            return null;
        }
        var consumeCar = getTopCarId();
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

    public boolean removeWaitingCar(String carId) {
        List<String> waitingCarsList = getWaitingCarsList();
        if (waitingCarsList.isEmpty()) {
            return false;
        }
        if (waitingCarsList.contains(carId)) {
            waitingCarsList.remove(carId);
            waitingCars = String.join(",", waitingCarsList);
            return true;
        }
        return false;
    }

    // return -1 means NOTFOUND
    public int getQueueIdx(String carId) {
        List<String> queueCars = getWaitingCarsList();
        if (queueCars.size() <= 0) {
            return -1;
        }
        for (int i = 0; i < queueCars.size(); i++) {
            if (queueCars.get(i).equals(carId)) {
                return i + 1;
            }
        }
        return -1;
    }

}
