package com.bupt.charger.service;
import com.bupt.charger.controller.AdminPileController;
import com.bupt.charger.entity.Admin;
import com.bupt.charger.entity.Pile;
import com.bupt.charger.repository.PilesRepository;
import com.bupt.charger.request.ShutDownPileRequest;
import com.bupt.charger.request.StartPileRequest;
import com.bupt.charger.request.SetPileParametersRequest;
import com.bupt.charger.response.AdminLoginResponse;
import com.bupt.charger.response.AdminPileResponse;
import com.bupt.charger.response.UserLoginResponse;
import com.bupt.charger.entity.User;
import com.bupt.charger.exception.ApiException;
import com.bupt.charger.repository.AdminRepository;
import com.bupt.charger.request.UserRegistrationRequest;
import io.swagger.annotations.Api;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

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
        Admin admin = adminRepository.findByAdminNameAndPassword(adminName,password);
        if (admin == null) {
            throw new LoginException("用户名或密码错误");
        }

        AdminLoginResponse loginResponse = new AdminLoginResponse();
        loginResponse.setAdminName(admin.getAdminName());

        return loginResponse;
    }

    @Autowired
    private PilesRepository pilesRepository;

    public void startPile(StartPileRequest startPileRequest) throws ApiException
    {
        log.info("Admin try to start pile: " + startPileRequest.getPileId());
        var pileId = startPileRequest.getPileId();
        Pile pile = pilesRepository.findByPileId(pileId);
        if(pile == null)
        {
            throw new ApiException("不存在这个充电桩！");
        }
        if(pile.getStatus() != Pile.Status.OFF)
        {
            throw new ApiException("充电桩状态异常！");
        }
        pile.setStatus(Pile.Status.UNRUNNING);

        pilesRepository.save(pile);

    }

    public void shutDownPile(ShutDownPileRequest shutDownPileRequest) throws ApiException
    {
        log.info("Admin try to shut down pile: " + shutDownPileRequest.getPileId());
        var pileId = shutDownPileRequest.getPileId();
        Pile pile = pilesRepository.findByPileId(pileId);
        if(pile == null)
        {
            throw new ApiException("不存在这个充电桩！");
        }

        if(pile.getStatus() != Pile.Status.UNRUNNING)
        {
            throw new ApiException("充电桩状态异常！");
        }
        pile.setStatus(Pile.Status.OFF);

        pilesRepository.save(pile);

    }



    public void setPileParameters(SetPileParametersRequest setPileParametersRequest) throws ApiException
    {
        log.info("Admin try to set pile parameters: " + setPileParametersRequest.getPileId());
        var pileId = setPileParametersRequest.getPileId();
        Pile pile = pilesRepository.findByPileId(pileId);
        if(pile == null)
        {
            throw new ApiException("不存在这个充电桩！");
        }
        if(pile.getStatus() != Pile.Status.OFF)
        {
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


}
