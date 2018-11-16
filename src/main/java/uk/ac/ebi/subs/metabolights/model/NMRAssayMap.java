package uk.ac.ebi.subs.metabolights.model;

import java.util.LinkedHashMap;

/**
 * Created by kalai on 22/10/2018.
 */

public class NMRAssayMap extends LinkedHashMap<String, String> {

    public NMRAssayMap(Assay assay) {
        super();
        process(assay);
    }

    private void process(Assay assay) {
        put(AssaySpreadSheetConstants.SAMPLE_NAME, "");

        put(AssaySpreadSheetConstants.EXTRACTION_PROTOCOL_REF,"");
        put(AssaySpreadSheetConstants.EXTRACTION_PROTOCOL_EXTRACTION_METHOD,"");
        put(AssaySpreadSheetConstants.EXTRACTION_PROTOCOL_EXTRACT_NAME,"");


    }
}
