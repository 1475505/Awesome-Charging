package com.bupt.charger.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author wyf（ created: 2023-05-30  14:53 )
 */
@Data
@ToString
public class SetPileParametersRequest {
    @JsonProperty("pile_id")
    private String pileId;

    @JsonProperty("rule")
    private String rule;

    @JsonProperty("peak_up")
    private double peakUp;

    @JsonProperty("usual_up")
    private double usualUp;

    @JsonProperty("valley_up")
    private double valleyUp;

    @JsonProperty("serve_up")
    private double serveUp;


}
