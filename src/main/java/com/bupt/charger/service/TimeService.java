package com.bupt.charger.service;


/**
 * @author nameless0078 （ created: 2023-06-03 16 :24 )
 */

import com.bupt.charger.exception.ApiException;
import com.bupt.charger.response.GetTimeNowResponse;
import com.bupt.charger.util.FormatUtils;
import com.bupt.charger.util.SysTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
public class TimeService {

    @Autowired
    SysTimer sysTimer;

    public GetTimeNowResponse getTimeNow() throws ApiException {
        log.info("Front end try to get time.");
        GetTimeNowResponse response = new GetTimeNowResponse();
        //Date nowTime = sysTimer.getStartTime();
        LocalDateTime nowTime = FormatUtils.getNowLocalDateTime();
        response.setTimestamp(nowTime);
        return response;
    }

}
