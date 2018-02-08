package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.List;

/**
 * Created by kalai on 29/01/2018.
 */
@Data
public class SampleSourceModel {
    private List<Comment> comments;
    private List<SampleSourceOntologyModel> characteristics;
    private String name;
}