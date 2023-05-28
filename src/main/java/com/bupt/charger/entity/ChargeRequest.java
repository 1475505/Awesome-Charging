package com.bupt.charger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ll （ created: 2023-05-27 17:12 )
 */
@Data
@Entity
@Table(name = "requests")
public class ChargeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    private String carId;

    @Enumerated(EnumType.ORDINAL)
    private Status status = Status.INIT;

    public enum Status {
        INIT, //0, 表示这个请求尚未被处理
        DOING, //1
        DONE,  //2
        OTHER  //3
    }

    private double requestAmount;
    private double doneAmount;

    private RequestMode requestMode = RequestMode.UNSET;

    public enum RequestMode {
        UNSET, FAST, SLOW
    }

    /*
 本次订单可能未完成，比如被后续调度等。
 这种情况可以创建个新请求，然后从这个字段获取后续的情况。
 比如：
 id = 1，要充5度电，已充2度，然后充电桩寄了
 后面调度可以建个补偿请求，若id=3，设置充3度电。
 然后id=1的succReqs增加元素3.
 */

    public void setRequestMode(RequestMode mode) {
        this.requestMode = mode;
    }
    public void setRequestMode(String mode) {
        if ("quick".equalsIgnoreCase(mode)) {
            this.setRequestMode(RequestMode.FAST);
        } else if ("slow".equalsIgnoreCase(mode)) {
            this.setRequestMode(RequestMode.SLOW);
        } else {
            this.setRequestMode(RequestMode.UNSET);
        }
    }
    private String succReqs;

    public List<Long> getSuccReqsList() {
        String input = succReqs;
        List<Long> resultList = new ArrayList<>();

        if (input != null && !input.isEmpty()) {
            String[] numberStrings = input.split(",");
            for (String numberString : numberStrings) {
                try {
                    long number = Long.parseLong(numberString.trim());
                    resultList.add(number);
                } catch (NumberFormatException e) {
                    // 如果字符串无法解析为长整型数字，则忽略该项
                }
            }
        }

        return resultList;
    }

    /* 若添加成功，返回true，否则false  */
    public boolean addSuccReqs(long id) {
        if (succReqs == null || succReqs.isEmpty()) {
            succReqs = String.valueOf(id);
        } else {
            succReqs += "," + id;
        }
        return true;
    }

    private LocalDateTime startChargingTime;
    private LocalDateTime endChargingTime;
}
