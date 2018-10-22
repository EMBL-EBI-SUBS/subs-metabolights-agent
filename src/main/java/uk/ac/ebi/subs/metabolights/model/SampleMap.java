package uk.ac.ebi.subs.metabolights.model;

import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by kalai on 22/10/2018.
 */

public class SampleMap extends LinkedHashMap {

    public SampleMap(){
        //todo take sample object and assign values
        put(SampleSpreadSheetConstants.SOURCE_NAME,"");
        put(SampleSpreadSheetConstants.ORGANISM,"");
        put(SampleSpreadSheetConstants.ORGANISM_TERM_SOURCE_REF,"");
        put(SampleSpreadSheetConstants.ORGANISM_TERM_ACCESSION_NUMBER,"");
        put(SampleSpreadSheetConstants.ORGANISM_PART,"");
        put(SampleSpreadSheetConstants.ORGANISM_PART_TERM_SOURCE_REF,"");
        put(SampleSpreadSheetConstants.ORGANISM_PART_TERM_ACCESSION_NUMBER,"");
        put(SampleSpreadSheetConstants.PROTOCOL_REF,"");
        put(SampleSpreadSheetConstants.SAMPLE_NAME,"");
    }
}
