package com.bupt.charger.service;

import com.bupt.charger.controller.AdminPileController;
import com.bupt.charger.entity.*;
import com.bupt.charger.repository.CarRepository;
import com.bupt.charger.repository.PilesRepository;
import com.bupt.charger.request.*;
import com.bupt.charger.response.*;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.AdminRepository;
import io.swagger.annotations.Api;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

/**
 * @author wyf （ created: 2023-05-26 13:27 )
 */
@Service
@Log
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    public AdminLoginResponse login(String adminName, String password) throws LoginException {
        log.info("Admin try to login: " + adminName);
        Admin admin = adminRepository.findByAdminNameAndPassword(adminName, password);
        if (admin == null) {
            throw new LoginException("用户名或密码错误");
        }

        AdminLoginResponse loginResponse = new AdminLoginResponse();
        loginResponse.setAdminName(admin.getAdminName());

        return loginResponse;
    }

    @Autowired
    private PilesRepository pilesRepository;

    public void startPile(StartPileRequest startPileRequest) throws ApiException {
        log.info("Admin try to start pile: " + startPileRequest.getPileId());
        var pileId = startPileRequest.getPileId();
        Pile pile = pilesRepository.findByPileId(pileId);
        if (pile == null) {
            throw new ApiException("不存在这个充电桩！");
        }
        if (pile.getStatus() != Pile.Status.OFF) {
            throw new ApiException("充电桩状态异常！");
        }
        pile.setStatus(Pile.Status.UNRUNNING);

        pilesRepository.save(pile);

    }

    public void shutDownPile(ShutDownPileRequest shutDownPileRequest) throws ApiException {
        log.info("Admin try to shut down pile: " + shutDownPileRequest.getPileId());
        var pileId = shutDownPileRequest.getPileId();
        Pile pile = pilesRepository.findByPileId(pileId);
        if (pile == null) {
            throw new ApiException("不存在这个充电桩！");
        }

        if (pile.getStatus() != Pile.Status.UNRUNNING) {
            throw new ApiException("充电桩状态异常！");
        }
        pile.setStatus(Pile.Status.OFF);

        pilesRepository.save(pile);

    }


    public void setPileParameters(SetPileParametersRequest setPileParametersRequest) throws ApiException {
        log.info("Admin try to set pile parameters: " + setPileParametersRequest.getPileId());
        var pileId = setPileParametersRequest.getPileId();
        Pile pile = pilesRepository.findByPileId(pileId);
        if (pile == null) {
            throw new ApiException("不存在这个充电桩！");
        }
        if (pile.getStatus() != Pile.Status.OFF) {
            throw new ApiException("充电桩状态异常！");
        }

        var feePattern = setPileParametersRequest.getRule();
        pile.setFeePattern(feePattern);

        double peakPrice = setPileParametersRequest.getPeakUp();
        pile.setPeakPrice(peakPrice);

        double usualPrice = setPileParametersRequest.getUsualUp();
        pile.setUsualPrice(usualPrice);

        double valleyPrice = setPileParametersRequest.getValleyUp();
        pile.setValleyPrice(valleyPrice);

        double servePrice = setPileParametersRequest.getServeUp();
        pile.setServePrice(servePrice);

        pilesRepository.save(pile);

    }


    public CheckChargerResponse checkCharger(String pileId) {
        log.info("Admin try to check charger : " + pileId);
        CheckChargerResponse response = new CheckChargerResponse();
        Pile pile = pilesRepository.findByPileId(pileId);
        if (pile == null) {
            throw new ApiException("未找到此充电桩");
        }
        response.setWorkingState(pile.getStatus().ordinal());
        response.setTotalChargeNum(pile.getTotalChargeNum());
        response.setTotalChargeTime(pile.getTotalChargeTime());
        response.setTotalCapacity(pile.getTotalCapacity());

        return response;
    }


    @Autowired
    private CarRepository carRepository;
    public CheckChargerQueueResponse checkChargerQueue(String pileId) {
        log.info("Admin try to check charger queue: " + pileId);

        CheckChargerQueueResponse response = new CheckChargerQueueResponse();
        Pile pile = pilesRepository.findByPileId(pileId);

        if (pile == null) {
            throw new ApiException("未找到此充电桩");
        }

        List<Car> cars=carRepository.findAllByPileId(pileId);

        response.setCars(cars);

        return response;
    }


}
