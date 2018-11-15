package uk.ac.ebi.subs.metabolights.model;

import java.util.LinkedHashMap;

/**
 * Created by kalai on 22/10/2018.
 */

public class AssayMap extends LinkedHashMap<String, String> {

    public AssayMap(Assay assay) {
        super();
        process(assay);
    }

    private void process(Assay assay) {
    }
}
