package uk.ac.ebi.subs.metabolights.model;


import lombok.Data;

import java.util.List;

/**
 * Created by kalai on 27/11/2018.
 */
@Data
public class NewAssayResult {
    private String success;
    private List<Assay> assay;
}
