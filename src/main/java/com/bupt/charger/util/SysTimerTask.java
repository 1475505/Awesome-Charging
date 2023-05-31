package com.bupt.charger.util;

import java.util.Date;
import java.util.TimerTask;

public class SysTimerTask extends TimerTask {

    @Override
    public void run() {
        SysTimer.setStartTime(new Date(SysTimer.getStartTime().getTime() + 1000 * 60));
//        System.out.println("current time: " + SysTimer.getStartTime());
    }
}
