package com.bupt.charger.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

/**
 * @author ll （ created: 2023-05-31 20:42 )
 */
@Configuration
public class PileConfig {
    //快充功率（度/小时）
    public final double FAST_POWER = 30;
    //慢充功率（度/小时）
    public final double SLOW_POWER = 7;

}
