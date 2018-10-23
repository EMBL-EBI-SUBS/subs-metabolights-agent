package uk.ac.ebi.subs.metabolights.model;

import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by kalai on 22/10/2018.
 */

public class SampleMap extends LinkedHashMap {

    public SampleMap(Sample sample) {
        process(sample);
    }

    private void process(Sample sample) {
        put(SampleSpreadSheetConstants.PROTOCOL_REF, "Sample collection");
        put(SampleSpreadSheetConstants.SAMPLE_NAME, sample.getName());
        if (sample.getDerivesFrom().size() > 0) {
            for (Source source : sample.getDerivesFrom()) {
                put(SampleSpreadSheetConstants.SOURCE_NAME, source.getName());
                if (source.getCharacteristics().size() > 0) {
                    for (SampleSourceOntologyModel sourceOntologyModel : source.getCharacteristics()) {
                        if (sourceOntologyModel.getCategory().getAnnotationValue().equalsIgnoreCase("organism")) {
                            put(SampleSpreadSheetConstants.ORGANISM, sourceOntologyModel.getValue().getAnnotationValue());
                            put(SampleSpreadSheetConstants.ORGANISM_TERM_SOURCE_REF, sourceOntologyModel.getValue().getTermSource().getFile());
                            put(SampleSpreadSheetConstants.ORGANISM_TERM_ACCESSION_NUMBER, sourceOntologyModel.getValue().getTermAccession());
                        }
                        if (sourceOntologyModel.getCategory().getAnnotationValue().equalsIgnoreCase("organism part")) {
                            put(SampleSpreadSheetConstants.ORGANISM_PART, sourceOntologyModel.getValue().getAnnotationValue());
                            put(SampleSpreadSheetConstants.ORGANISM_PART_TERM_SOURCE_REF, sourceOntologyModel.getValue().getTermSource().getFile());
                            put(SampleSpreadSheetConstants.ORGANISM_PART_TERM_ACCESSION_NUMBER, sourceOntologyModel.getValue().getTermAccession());
                        }
                    }
                }
            }
        }
    }
}
