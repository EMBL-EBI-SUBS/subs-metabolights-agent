package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class SampleFactorCategory {
    private OntologyModel factorType;
    private List<Comment> comments;
    private String factorName;

    public SampleFactorCategory(){
        setFactorType(new OntologyModel());
        setComments(new ArrayList<>());
        setFactorName("");
    }
}
