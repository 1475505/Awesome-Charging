package com.bupt.charger.service;

import com.bupt.charger.entity.Bill;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.BillRepository;
import com.bupt.charger.repository.ChargeReqRepository;
import com.bupt.charger.response.*;
import com.bupt.charger.util.FormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author ll （ created: 2023-05-27 21:20 )
 */
@Service
@Slf4j
public class BillService {
    @Autowired
    BillRepository billRepository;

    @Autowired
    ChargeReqRepository chargeReqRepository;

    public DetailedBillResponse getBill(long billId) {
        DetailedBillResponse response = new DetailedBillResponse();
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
        AllBillsResponse response = new AllBillsResponse();
        Set<Long> recoveredRequests = new TreeSet<>();
        List<ChargeRequest> chargeRequests = chargeReqRepository.findAllByCarIdOrderById(carId);

        for (ChargeRequest req : chargeRequests) {
            if (recoveredRequests.contains(req.getId())) continue;
            if (req.getStatus() != ChargeRequest.Status.DONE) continue;
            BillResponse billResponse = new BillResponse();
            ChargeRequest curReq = req;
            if (curReq.getBillId() == null) {
                continue;
            }
            Optional<Bill> billOptional = billRepository.findById(curReq.getBillId());
            if (billOptional.isEmpty()) {
                throw new ApiException("没有找到详单" + curReq.getBillId());
            }
            Bill bill = billOptional.get();
            billResponse.billId.add(bill.getId());
            billResponse.pileId.add(bill.getPileId());
            billResponse.setDate(bill.getStartTime().toLocalDate().toString());
            billResponse.setCarId(carId);
            billResponse.setStartTime(FormatUtils.LocalDateTime2Long(bill.getStartTime()));
            double chargeAmount = bill.getChargeAmount();
            long chargeDuration = bill.getChargeDuration();
            double chargeFee = bill.getChargeFee();
            double serviceFee = bill.getServiceFee();
            LocalDateTime endTime = bill.getEndTime();
            while (curReq.isSuffered()) {
                Long succReq = req.getSuccReqsList().get(0);
                log.debug("发现请求" + curReq.getId() + "存在故障请求" + succReq);
                Optional<ChargeRequest> succReqOptional = chargeReqRepository.findById(succReq);
                curReq = succReqOptional.get();
                recoveredRequests.add(curReq.getId());
                billOptional = billRepository.findById(curReq.getBillId());
                bill = billOptional.get();
                billResponse.billId.add(bill.getId());
                billResponse.pileId.add(bill.getPileId());

                chargeAmount += bill.getChargeAmount();
                chargeDuration += bill.getChargeDuration();
                chargeFee += bill.getChargeFee();
                serviceFee += bill.getServiceFee();
                endTime = bill.getEndTime();
            }
            billResponse.setEndTime(FormatUtils.LocalDateTime2Long(endTime));
            billResponse.setChargeAmount(chargeAmount);
            billResponse.setChargeDuration(chargeDuration);
            billResponse.setChargeFee(chargeFee);
            billResponse.setServiceFee(serviceFee);
            billResponse.setTotalFee(chargeFee + serviceFee);
            response.getBills().add(billResponse);
        }
        return response;
    }

    public AllBillsByDayResponse checkBillByDay(String carId) {
        List<Bill> bills = billRepository.findAllByCarIdOrderByStartTime(carId);
        AllBillsByDayResponse response = new AllBillsByDayResponse();
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
