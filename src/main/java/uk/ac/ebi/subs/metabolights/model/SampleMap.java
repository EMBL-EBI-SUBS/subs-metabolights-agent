package uk.ac.ebi.subs.metabolights.model;

import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by kalai on 22/10/2018.
 */

public class SampleMap extends LinkedHashMap {

    public SampleMap(Sample sample) {
        //todo check for null values before assigning
        //todo check order for organism and organism part
        put(SampleSpreadSheetConstants.SOURCE_NAME, sample.getDerivesFrom().get(0).getName());
        put(SampleSpreadSheetConstants.ORGANISM, sample.getDerivesFrom().get(0).getCharacteristics().get(0).getValue().getAnnotationValue());
        put(SampleSpreadSheetConstants.ORGANISM_TERM_SOURCE_REF, sample.getDerivesFrom().get(0).getCharacteristics().get(0).getValue().getTermSource().getFile());
        put(SampleSpreadSheetConstants.ORGANISM_TERM_ACCESSION_NUMBER, sample.getDerivesFrom().get(0).getCharacteristics().get(0).getValue().getTermAccession());
        put(SampleSpreadSheetConstants.ORGANISM_PART, sample.getDerivesFrom().get(0).getCharacteristics().get(1).getValue().getAnnotationValue());
        put(SampleSpreadSheetConstants.ORGANISM_PART_TERM_SOURCE_REF, sample.getDerivesFrom().get(0).getCharacteristics().get(1).getValue().getTermSource().getFile());
        put(SampleSpreadSheetConstants.ORGANISM_PART_TERM_ACCESSION_NUMBER, sample.getDerivesFrom().get(0).getCharacteristics().get(1).getValue().getTermAccession());
        put(SampleSpreadSheetConstants.PROTOCOL_REF, "Sample collection");
        put(SampleSpreadSheetConstants.SAMPLE_NAME, sample.getName());
    }
}
