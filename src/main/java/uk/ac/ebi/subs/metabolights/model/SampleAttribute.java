package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.List;

/**
 * Created by kalai on 29/01/2018.
 */
@Data
public class SampleAttribute {
    private OntologyModel unit;
    private Factor factor_name;
    private List<Comment> comments;
    private String value;
}
