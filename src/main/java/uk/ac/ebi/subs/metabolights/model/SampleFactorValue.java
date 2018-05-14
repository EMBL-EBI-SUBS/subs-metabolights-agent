package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.List;

@Data
public class SampleFactorValue {
    private List<Comment> comments;
    private SampleFactorCategory category;
    private OntologyModel value;
    private OntologyModel unit;
}
