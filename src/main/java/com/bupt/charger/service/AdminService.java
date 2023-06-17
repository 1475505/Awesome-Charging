package com.bupt.charger.service;

import com.bupt.charger.entity.Admin;
import com.bupt.charger.entity.Car;
import com.bupt.charger.entity.ChargeRequest;
import com.bupt.charger.entity.Pile;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.AdminRepository;
import com.bupt.charger.repository.CarRepository;
import com.bupt.charger.repository.ChargeReqRepository;
import com.bupt.charger.repository.PilesRepository;
import com.bupt.charger.request.*;
import com.bupt.charger.response.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author wyf （ created: 2023-05-26 13:27 )
 */
@Service
@Log
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ChargeReqRepository chargeReqRepository;

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
        // 有几个空位就调度几次，防止有空余的没有被调度
        for (int i = 0; i < pile.getCapacity(); i++) {
            scheduleService.moveToChargingQueue();
        }
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

    public  List<CarResponse> checkChargerQueue(String pileId) {
        log.info("Admin try to check charger queue: " + pileId);

        CheckChargerQueueResponse response = new CheckChargerQueueResponse();
        Pile pile = pilesRepository.findByPileId(pileId);

        if (pile == null) {
            throw new ApiException("未找到此充电桩");
        }

        List<String> qEles = pile.getQList();
        List<Car> cars = new ArrayList<>();
        List<CarResponse> carResponseList = new ArrayList<>();
        String car_id;
        double request_amount;
        int wait_time;
        log.info("cars: " + qEles);
        for (String s : qEles) {
            Car car = carRepository.findByCarId(s);
            if (car != null) {
                cars.add(car);
                car_id=car.getCarId();
                Optional<ChargeRequest> chargeRequestOptional = chargeReqRepository.findById(car.getHandingReqId());
                if (chargeRequestOptional.isEmpty()) {
                    throw new ApiException("chargeRequestOptional is empty");
                }
                ChargeRequest chargeRequest = chargeRequestOptional.get();
                request_amount=chargeRequest.getRequestAmount();

                CarResponse carResponse=new CarResponse(22,car_id,request_amount,0);
                carResponseList.add(carResponse);
            }
        }

        return carResponseList;
    }


    /**
     * 管理员初始化数据库
     *
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
         * TODO:现在还要写初始化设置
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


    public void diePile(DiePileRequest diePileRequest) throws ApiException {
        log.info("Admin try to die pile: " + diePileRequest.getPileId());
        var pileId = diePileRequest.getPileId();
        Pile pile = pilesRepository.findByPileId(pileId);
        if (pile == null) {
            throw new ApiException("不存在这个充电桩！");
        }

        pile.setStatus(Pile.Status.ERROR);
        pilesRepository.save(pile);

        //唤起后续调度

        //    如果是优先级故障，写下面语句
        scheduleService.priorityErrorMoveQueue(pileId);

        //    如果是时间故障，写
        // scheduleService.timeErrorMoveQueue(pileId);
    }

}
