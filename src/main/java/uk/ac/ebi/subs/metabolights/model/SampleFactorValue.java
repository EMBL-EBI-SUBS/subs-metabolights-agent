package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SampleFactorValue {
    private List<Comment> comments;
    private SampleFactorCategory category;
    private Object value;    //todo value can be any object. Let us assume OntologyModel
    private OntologyModel unit;

    public SampleFactorValue(){
        setComments(new ArrayList<>());
        setCategory(new SampleFactorCategory());
        setUnit(new OntologyModel());
    }
}
