package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalai on 29/01/2018.
 */
@Data
public class SampleSourceOntologyModel {
    private List<Comment> comments;
    private OntologyModel category;
    private OntologyModel unit;
    private OntologyModel value;

    public SampleSourceOntologyModel(){
        setComments(new ArrayList<>());
        setCategory(new OntologyModel());
        setUnit(new OntologyModel());
        setValue(new OntologyModel());
    }
}
