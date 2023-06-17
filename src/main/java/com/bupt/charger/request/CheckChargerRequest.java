package com.bupt.charger.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author wyf（ created: 2023-05-30  19:50 )
 */
@Data
@ToString
public class CheckChargerRequest {
    @JsonProperty("pile_id")
    private String pileId;

}