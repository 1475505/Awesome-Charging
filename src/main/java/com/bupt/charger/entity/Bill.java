package com.bupt.charger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ll （ created: 2023-05-27 20:24 )
 */

//每次充电完成，写入Bill
@Data
@Entity
@Table(name = "bills")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    private String carId;
    private String pileId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double chargeAmount;
    private double chargeFee;
    private double serviceFee;

    // 单位：秒
    public long getChargeDuration() {
        return Duration.between(startTime, endTime).getSeconds();
    }

}
