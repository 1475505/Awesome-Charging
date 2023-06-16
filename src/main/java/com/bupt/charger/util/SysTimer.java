package com.bupt.charger.util;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Calendar;
import java.util.Timer;

@Component
public class SysTimer {
    private static Date startTime;
    private Timer timer;

    public void startClock()
    {
        Calendar calendar = Calendar.getInstance(); // 获取系统默认的Calendar实例
        calendar.set(2023, Calendar.MAY, 31, 3, 0, 0); // 设置日期为2023年5月31日8点
        SysTimer.startTime = calendar.getTime();//这里是从2023年5月31日8:00开始
//        SysTimer.startTime = new Date();//这里是从当前时间开始
        timer = new Timer();
        timer.schedule(new SysTimerTask(),0, 1000);
    }

    public static Date getStartTime() {
        return startTime;
    }

    public static void setStartTime(Date startTime) {
        SysTimer.startTime = startTime;
    }
}
