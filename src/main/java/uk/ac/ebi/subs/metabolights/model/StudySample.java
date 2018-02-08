package uk.ac.ebi.subs.metabolights.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by kalai on 31/01/2018.
 */
@Data
public class StudySample {
    @JsonProperty("@id")
    private String id;
    private List<String> characteristics;
    private List<FactorValue> factorValues;
    private String name;
}
