package uk.ac.ebi.subs.metabolights.model;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Header {

    private boolean mandatory;
    @JsonProperty("data-type")
    private String dataType;
    private int index;

}
