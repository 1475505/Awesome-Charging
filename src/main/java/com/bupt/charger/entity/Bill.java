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

    //    导出账单到本地文件
    public boolean exportBill(String nowTime) {
        String fileName = "./详单.csv";
        // 按顺序添加字符串
        List<String> rowList = new ArrayList<>();
        rowList.add(carId);
        rowList.add(String.valueOf(id));
        // 现在时间
        rowList.add(nowTime);
        rowList.add(pileId);
        rowList.add(String.valueOf(chargeAmount));
        rowList.add(String.valueOf(getChargeDuration()));
        rowList.add(String.valueOf(startTime));
        rowList.add(String.valueOf(endTime));
        rowList.add(String.valueOf(chargeFee));
        rowList.add(String.valueOf(serviceFee));
        rowList.add(String.valueOf(chargeFee + serviceFee));
        // 使用逗号分割，添加到行，自动换行后添加
        String row = String.join(",", rowList);
        try {
            File file = new File(fileName);

            FileOutputStream fos = null;
            if (!file.exists()) {
                //    没有文件就创建
                file.createNewFile();
                fos = new FileOutputStream(file);
                // 写入文件头部
                String head = "车辆编码, 详单编号, 详单生成时间,充电桩编号, 充电电量, 充电时长, 启动时间, 停止时间, 充电费用(元), 服务费(元),总费用(元)";
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);//指定以UTF-8格式写入文件
                osw.write(head);
                osw.write("\r\n");
                osw.close();
            }
            // 追加写入
            fos = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);//指定以UTF-8格式写入文件
            osw.write(row);
            osw.write("\r\n");
            osw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
