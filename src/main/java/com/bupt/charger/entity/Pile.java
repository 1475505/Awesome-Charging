package com.bupt.charger.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * @author ll （ created: 2023-05-27 17:26 )
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
    private Status status = Status.UNRUNNING;

    public enum Status {
        ERROR(-1), //故障
        UNRUNNING(0),  //启动未运行
        ON(1),  //运行未充电
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

    private String cars;

}
