package com.bupt.charger.service;

import com.bupt.charger.entity.Bill;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.BillRepository;
import com.bupt.charger.response.AllBillsResponse;
import com.bupt.charger.response.BillResponse;
import com.bupt.charger.response.DateBillResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

/**
 * @author ll （ created: 2023-05-27 21:20 )
 */
@Service
public class BillService {
    BillRepository billRepository;

    public BillResponse getBill(long billId) {
        BillResponse response = new BillResponse();
        Bill bill = billRepository.findBillById(billId);
        if (bill == null) {
            throw new ApiException("未找到此详单");
        }
        response.setCarId(bill.getCarId());
        response.setDate(bill.getCreatedAt().toLocalDate().toString());
        response.setBillId(bill.getId());
        response.setPileId(bill.getPileId());
        response.setChargeAmount(bill.getChargeAmount());
        response.setChargeDuration((int) Duration.between(bill.getStartTime(), bill.getEndTime()).getSeconds());
        response.setStartTime(bill.getStartTime().toEpochSecond(ZoneOffset.UTC));
        response.setEndTime(bill.getEndTime().toEpochSecond(ZoneOffset.UTC));
        response.setChargeFee(bill.getChargeFee());
        response.setServiceFee(bill.getServiceFee());
        response.setSubtotalFee(bill.getChargeFee() + bill.getServiceFee()); // 计算小计费用

        return response;
    }

    public AllBillsResponse checkBill(String carId) {
        List<Bill> bills = billRepository.findAllByCarIdOrderByStartTime(carId);
        AllBillsResponse response = new AllBillsResponse();
        Date lastDate = null;
        DateBillResponse dateBillResponse = new DateBillResponse();
        double chargeAmount = 0;
        long chargeDuration = 0;
        double chargeFee = 0;
        double serviceFee = 0;
        int idx = 0;

        for (Bill bill : bills) {
            var startTime = bill.getStartTime();
            var curDate = Date.from(startTime.atZone(ZoneOffset.UTC).toInstant());
            if (lastDate.equals(curDate)) {
                dateBillResponse.setBillId(dateBillResponse.getOrderId() + "," + bill.getId().toString());
                dateBillResponse.setChargeAmount(chargeAmount);
                dateBillResponse.setChargeDuration(chargeDuration);
                dateBillResponse.setChargeFee(chargeFee);
                dateBillResponse.setServiceFee(serviceFee);
                dateBillResponse.setTotalFee(chargeFee + serviceFee);
                response.getBills().add(dateBillResponse);
                dateBillResponse = null;
                chargeAmount = 0;
                serviceFee = 0;
                chargeFee = 0;
                chargeDuration = 0;
            } else {
                dateBillResponse = new DateBillResponse();
                dateBillResponse.setDate(curDate.toString());
                dateBillResponse.setBillId(bill.getId().toString());
                dateBillResponse.setCarId(carId);
                chargeAmount += bill.getChargeAmount();
                chargeDuration += bill.getChargeDuration();
                chargeFee += bill.getChargeFee();
                serviceFee += bill.getServiceFee();
            }
        }
        if (dateBillResponse != null) {
            response.getBills().add(dateBillResponse);
        }
        return response;
    }
}
