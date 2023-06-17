package com.bupt.charger.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author wyf（ created: 2023-05-29  20:11 )
 */
@Data
@ToString
public class StartPileRequest {
    @JsonProperty("pile_id")
    private String pileId;
}

