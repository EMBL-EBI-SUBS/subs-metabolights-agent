package uk.ac.ebi.subs.metabolights.model;

import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by kalai on 22/10/2018.
 */

public class SampleMap extends LinkedHashMap<String, String> {

    public SampleMap(Sample sample) {
        super();
        process(sample);
    }

    private void process(Sample sample) {
        put(SampleSpreadSheetConstants.PROTOCOL_REF, "Sample collection");
        put(SampleSpreadSheetConstants.SAMPLE_NAME, sample.getName());
        /*
        Set organism and organism part information
         */
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
                        if (sourceOntologyModel.getCategory().getAnnotationValue().equalsIgnoreCase("variant")) {
                            put(SampleSpreadSheetConstants.VARIANT, sourceOntologyModel.getValue().getAnnotationValue());
                            put(SampleSpreadSheetConstants.VARIANT_TERM_SOURCE_REF, sourceOntologyModel.getValue().getTermSource().getFile());
                            put(SampleSpreadSheetConstants.VARIANT_TERM_ACCESSION_NUMBER, sourceOntologyModel.getValue().getTermAccession());
                        }
                    }
                }
            }
        }
      /*
        Set factor information
         */
        if (sample.getFactorValues().size() > 0) {
            for (SampleFactorValue sampleFactorValue : sample.getFactorValues()) {
                OntologyModel model = (OntologyModel) sampleFactorValue.getValue();
                put("Factor Value[" + sampleFactorValue.getCategory().getFactorName() + "]", model.getAnnotationValue());
            }
        }
        if (sample.getComments().size() > 0) {
            if (sample.getComments().get(0).getValue() != null || !sample.getComments().get(0).getValue().isEmpty()) {
                put(SampleSpreadSheetConstants.ROW_INDEX, sample.getComments().get(0).getValue());
            }

        }
    }
}
