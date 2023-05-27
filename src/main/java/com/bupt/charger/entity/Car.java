package com.bupt.charger.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.awt.geom.Area;
import java.util.Queue;

/**
 * @author ll （ created: 2023-05-27 17:26 )
 */
@Data
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String carId;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    public enum Status {
        COMPLETED, //0
        WAITING,  //1
        PENDING,  //2
        CHARGING, //3
        OTHER  //4
    }

    @Enumerated(EnumType.ORDINAL)
    private Area area;

    public enum Area {
        COMPLETED, //0,表示没来充电
        WAITING,  //1
        CHARGING, //2
        OTHER  //3
    }

    @Enumerated(EnumType.ORDINAL)
    private Queue queue;

    public enum Queue {
        UNQUEUED, //0,表示没来充电
        WAITING,  //1
        CHARGING, //2
        OTHER  //3
    }

    private long PileId;

}
