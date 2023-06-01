package com.bupt.charger.entity;

import jakarta.persistence.*;
import lombok.Data;

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
    private Status status = Status.COMPLETED;

    public enum Status {
        COMPLETED, //0
        waiting,  //1
        pending,  //2
        charging, //3
        OTHER  //4
    }

    @Enumerated(EnumType.ORDINAL)
    private Area area = Area.COMPLETED;


    public enum Area {
        COMPLETED, //0,表示没来充电
        WAITING,  //1
        CHARGING, //2
        OTHER;  //3

        @Override
        public String toString() {
            if (this == WAITING) {
                return "waiting area";
            }
            if (this == CHARGING) {
                return "charging area";
            }
            return super.toString();
        }
    }

    private Queue queue = Queue.UNQUEUED;

    private String queueNo;

    public enum Queue {
        UNQUEUED, //0,表示没来充电
        WAITING,  //1
        CHARGING, //2
        OTHER  //3
    }

    /**
     * -1表示没有正在处理的请求。
     */
    private long handingReqId = -1;
    private String pileId;

    public boolean canCharging() {
        if (status == Status.waiting && area == Area.CHARGING) {
            return true;
        }
        return false;
    }

    public boolean inChargingProcess() {
        if (status != Status.OTHER && status != Status.COMPLETED) {
            return true;
        }
        return false;
    }

    public void releaseChargingProcess() {
        this.setStatus(Car.Status.COMPLETED);
        this.setArea(Car.Area.COMPLETED);
        this.setPileId("");
        this.setQueue(Car.Queue.UNQUEUED);
        this.setQueueNo("");
        this.setHandingReqId(-1);
    }
}
