package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.List;

@Data
public class AssayOtherMaterial {
    private List<Comment> comments;
    private List<SampleSourceOntologyModel> characteristics;
    private String name;
    private String type;
}
