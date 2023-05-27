package com.bupt.charger.entity;

import jakarta.persistence.*;
import lombok.Data;

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

    private String carId;

    @Enumerated(EnumType.ORDINAL)
    private Status carPosition; // 1 or 3 or

    public enum Status {
        COMPLETED, //0, 表示这个请求已经充完电了，或者还没开始进等候区等情况
        WAITING,  //1
        PENDING,  //2
        CHARGING, //3
        OTHER  //4
    }

    private double requestAmount;
    private double doneAmount;

    private RequestMode requestMode;

    public enum RequestMode {
        FAST, SLOW
    }

    /*
 本次订单可能未完成，比如被后续调度等。
 这种情况可以创建个新请求，然后从这个字段获取后续的情况。
 比如：
 id = 1，要充5度电，已充2度，然后充电桩寄了
 后面调度可以建个补偿请求，若id=3，设置充3度电。
 然后id=1的succReqs增加元素3.
 */
    private String succReqs;

    public List<Long> getSuccReqs() {
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
        if (succReqs.isEmpty()) {
            succReqs = String.valueOf(id);
        } else {
            succReqs += "," + id;
        }
        return true;
    }
}
