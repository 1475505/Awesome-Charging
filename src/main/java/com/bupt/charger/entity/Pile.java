package com.bupt.charger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ll （ created: 2023-05-27 17:26 )
 */
/*
 */
@Data
@Entity
@Table(name = "piles")
public class Pile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pileId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.FREE;

    public enum Status {
        ERROR(-1), //故障
        OFF(0),  //关闭
        FREE(1),  //启动且空闲
        CHARGING(2); //充电中

        private int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private String feePattern;

    private double peakPrice = 0;
    private double usualPrice = 0;
    private double valleyPrice = 0;
    private double servePrice = 0;

    private int totalChargeNum = 0;
    private int totalChargeTime = 0;
    private int totalCapacity = 0;

    public Mode mode; // 这是个快充还是慢充桩

    public enum Mode {
        F, // 快充
        T // 这个叫慢充，不知道是哪个单词，反正是慢充。对应文档
    }

    private int capacity = 2; // 队列上限
    private String carQueue; // 第一个是充电中的车辆，后面是等待的车辆

    public boolean isON() {
        return status == Status.FREE || status == Status.CHARGING;
    }

    public List<String> getQList() {
        String input = carQueue;
        List<String> resultList = new ArrayList<>();

        if (input != null && !input.isEmpty()) {
            String[] numberStrings = input.split(",");
            return Arrays.asList(numberStrings);
        }
        return resultList;
    }

    public int getQCnt() {
        String input = carQueue;
        List<String> resultList = new ArrayList<>();

        if (input != null && !input.isEmpty()) {
            String[] numberStrings = input.split(",");
            return numberStrings.length;
        }
        return 0;
    }

    /* 若添加成功，返回true，否则false  */
    public boolean addCar(String id) {
        if (getQCnt() >= capacity) {
            return false;
        }
        if (carQueue == null || carQueue.isEmpty()) {
            carQueue = String.valueOf(id);
        } else {
            carQueue += "," + id;
        }
        return true;
    }

    public String consumeWaitingCar() {
        if (carQueue == null || carQueue.isEmpty()) {
            return null;
        }
        var consumeCar = getQList().get(0);
        if (getQCnt() == 1) {
            carQueue = "";
            return consumeCar;
        } else {
            // 移除首个元素
            int commaIndex = carQueue.indexOf(",");
            if (commaIndex != -1) {
                carQueue = carQueue.substring(commaIndex + 1).trim();
            }
            return consumeCar;
        }
    }

    // return -1 means NOTFOUND
    public int getQueueIdx(String carId) {
        List<String> queueCars = getQList();
        for (int i = 0; i < queueCars.size(); i++) {
            if (queueCars.get(i).equals(carId)) {
                return i + 1;
            }
        }
        return -1;
    }


}
