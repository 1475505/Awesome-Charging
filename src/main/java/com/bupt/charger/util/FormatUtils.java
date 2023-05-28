package com.bupt.charger.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author ll （ created: 2023-05-28 10:40 )
 */
@Component
public class FormatUtils {
    public static long LocalDateTime2Long(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.UTC) * 1000;
    }
}
