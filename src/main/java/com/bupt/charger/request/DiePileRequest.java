package com.bupt.charger.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author wyf（ created: 2023-06-03  16:03 )
 */
@Data
@ToString
public class DiePileRequest {
    @JsonProperty("pileId")
    private String pileId;

}