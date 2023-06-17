package com.bupt.charger.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author wyf（ created: 2023-05-30  12:41 )
 */
@Data
@ToString
public class ShutDownPileRequest {
    @JsonProperty("pile_id")
    private String pileId;
}