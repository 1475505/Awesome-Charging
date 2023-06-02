package com.bupt.charger.service;

import com.bupt.charger.entity.Bill;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.BillRepository;
import com.bupt.charger.response.AllBillsResponse;
import com.bupt.charger.response.BillResponse;
import com.bupt.charger.response.DateBillResponse;
import com.bupt.charger.util.FormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Format;
import java.time.*;
import java.util.Date;
import java.util.List;

/**
 * @author ll （ created: 2023-05-27 21:20 )
 */
@Service
@Slf4j
public class BillService {
    @Autowired
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
        response.setStartTime(FormatUtils.LocalDateTime2Long(bill.getStartTime()));
        response.setEndTime(FormatUtils.LocalDateTime2Long(bill.getEndTime()));
        response.setChargeFee(bill.getChargeFee());
        response.setServiceFee(bill.getServiceFee());
        response.setSubtotalFee(bill.getChargeFee() + bill.getServiceFee()); // 计算小计费用

        return response;
    }

    public AllBillsResponse checkBill(String carId) {
        List<Bill> bills = billRepository.findAllByCarIdOrderByStartTime(carId);
        AllBillsResponse response = new AllBillsResponse();
        LocalDate walkedDate = null; // 上一次遍历的bill的日期
        DateBillResponse dateBillResponse = null;
        double chargeAmount = 0;
        long chargeDuration = 0;
        double chargeFee = 0;
        double serviceFee = 0;
        int idx = 0;

        for (Bill bill : bills) {
            var startTime = bill.getStartTime();
            LocalDate curDate = startTime.toLocalDate();
            if (walkedDate == null || !walkedDate.equals(curDate)) { // 新的一天的详单

                if (dateBillResponse != null) {
                    response.getBills().add(dateBillResponse);
                    dateBillResponse.setChargeAmount(chargeAmount);
                    dateBillResponse.setChargeDuration(chargeDuration);
                    dateBillResponse.setChargeFee(chargeFee);
                    dateBillResponse.setServiceFee(serviceFee);
                    dateBillResponse.setTotalFee(chargeFee + serviceFee);
                }

                dateBillResponse = new DateBillResponse();
                chargeAmount = bill.getChargeAmount();
                serviceFee = bill.getServiceFee();
                chargeFee = bill.getChargeFee();
                chargeDuration = bill.getChargeDuration();

                dateBillResponse.setDate(curDate.toString());
                dateBillResponse.setCarId(carId);
                dateBillResponse.billId.add(bill.getId());
                dateBillResponse.startTime.add(FormatUtils.LocalDateTime2Long(bill.getStartTime()));
                dateBillResponse.endTime.add(FormatUtils.LocalDateTime2Long(bill.getEndTime()));
                dateBillResponse.pileId.add(bill.getPileId());
            } else { //是本次dateBillResponse那天的详单
                dateBillResponse.billId.add(bill.getId());
                dateBillResponse.pileId.add(bill.getPileId());
                dateBillResponse.startTime.add(FormatUtils.LocalDateTime2Long(bill.getStartTime()));
                dateBillResponse.endTime.add(FormatUtils.LocalDateTime2Long(bill.getEndTime()));
                chargeAmount += bill.getChargeAmount();
                chargeDuration += bill.getChargeDuration();
                chargeFee += bill.getChargeFee();
                serviceFee += bill.getServiceFee();
            }
            walkedDate = curDate;
        }

        if (dateBillResponse != null) {
            dateBillResponse.setChargeAmount(chargeAmount);
            dateBillResponse.setChargeDuration(chargeDuration);
            dateBillResponse.setChargeFee(chargeFee);
            dateBillResponse.setServiceFee(serviceFee);
            dateBillResponse.setTotalFee(chargeFee + serviceFee);
            response.getBills().add(dateBillResponse);
        }

        return response;
    }
}
