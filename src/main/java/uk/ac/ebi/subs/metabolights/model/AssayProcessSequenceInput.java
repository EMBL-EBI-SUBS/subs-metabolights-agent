package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.List;

@Data
public class AssayProcessSequenceInput {
    private List<SampleSourceOntologyModel> characteristics;
    private List<SampleSourceOntologyModel> factorValues;
    private String name;
    private List<Comment> comments;
    private List<Source> devivesFrom;
}
