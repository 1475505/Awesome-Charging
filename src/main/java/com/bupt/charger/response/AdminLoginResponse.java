package com.bupt.charger.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
/**
 * @author wyf （ created: 2023-05-29 13:23 )
 */
@Data
public class AdminLoginResponse extends Resp {
    private String adminName;
}
