package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.List;
@Data
public class SampleFactorCategory {
    private OntologyModel factorType;
    private List<Comment> comments;
    private String factorName;
}
