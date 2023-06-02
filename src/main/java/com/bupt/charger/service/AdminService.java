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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.ArrayList;
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
        if (pile.isON()) {
            throw new ApiException("充电桩已经是开启状态");
        }
        pile.setStatus(Pile.Status.FREE);

        pilesRepository.save(pile);

    }

    public void shutDownPile(ShutDownPileRequest shutDownPileRequest) throws ApiException {
        log.info("Admin try to shut down pile: " + shutDownPileRequest.getPileId());
        var pileId = shutDownPileRequest.getPileId();
        Pile pile = pilesRepository.findByPileId(pileId);
        if (pile == null) {
            throw new ApiException("不存在这个充电桩！");
        }

        if (!pile.isON()) {
            throw new ApiException("充电桩并没有开");
        }

        if (pile.getStatus() == Pile.Status.CHARGING) {
            throw new ApiException("充电桩正在充电，请等待用户充电完成");
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
            throw new ApiException("充电桩不是关闭状态，不能操作");
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
        response.setWorkingState(pile.getStatus().getValue());
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

        List<String> qEles = pile.getQList();
        List<Car> cars = new ArrayList<>();
        log.info("cars: " + qEles);
        for (String s : qEles) {
            Car car = carRepository.findByCarId(s);
            if (car != null) {
                cars.add(car);
            }
        }
        response.setCars(cars);

        return response;
    }


    /**
     * 管理员初始化数据库
     * @return null
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public InitDataBaseResponse initDataBase(InitDataBaseRequest initDataBaseRequest) {
        log.info("Admin try to init database.");
        InitDataBaseResponse response = new InitDataBaseResponse();


        String tableName;
        tableName = "test";
        jdbcTemplate.update("TRUNCATE TABLE " + tableName);
        log.info("Admin try to TRUNCATE TABLE: " + tableName);


        /**
         * 现在还要写初始化设置
         * 快充速度：30
         * 慢充速度：7
         * 峰值价格：1。0元
         * 平时价格：0.7元
         * 谷时价格：0.4元
         * 服务费：0.8元
         * 充电桩数量：2（快）,（3）慢
         * 充电桩排队队列长度：2
         * 等候区最大容量：6
         * 充电桩命名：f1，f2，t1，t2，t3
         */




//        tableName="bills";
//        jdbcTemplate.update("TRUNCATE TABLE " + tableName);
//        log.info("Admin try to TRUNCATE TABLE: " + tableName);
//
//        tableName="cars";
//        jdbcTemplate.update("TRUNCATE TABLE " + tableName);
//        log.info("Admin try to TRUNCATE TABLE: " + tableName);
//
//        tableName="piles";
//        jdbcTemplate.update("TRUNCATE TABLE " + tableName);
//        log.info("Admin try to TRUNCATE TABLE: " + tableName);
//
//        tableName="queues";
//        jdbcTemplate.update("TRUNCATE TABLE " + tableName);
//        log.info("Admin try to TRUNCATE TABLE: " + tableName);
//
//        tableName="requests";
//        jdbcTemplate.update("TRUNCATE TABLE " + tableName);
//        log.info("Admin try to TRUNCATE TABLE: " + tableName);
//
//        tableName="users";
//        jdbcTemplate.update("TRUNCATE TABLE " + tableName);
//        log.info("Admin try to TRUNCATE TABLE: " + tableName);


        response.setDbResp("initial ok");

        return response;
    }

}
