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
            int index = 3;
            String termAccessionNumber = "Term Accession Number.";
            String termSourceReference = "Term Source REF.";
            int unitIndex = 0;
            String unitEntry = "Unit.";

            for (SampleFactorValue sampleFactorValue : sample.getFactorValues()) {
                OntologyModel model = (OntologyModel) sampleFactorValue.getValue();
                put("Factor Value[" + sampleFactorValue.getCategory().getFactorName() + "]", model.getAnnotationValue());
                   /*
                   Sample sheet requires ontology fields underneath for factor values to pass ISA validation.
                   SampleSpreadSheetConstants has refs to term source ref and term accession number
                   For any term source ref and term accession number added here, the numbering will start from 3.
                 */

                put(termSourceReference + index, "");
                put(termAccessionNumber + index, model.getTermAccession());
                index++;
                if (sampleFactorValue.getUnit().getAnnotationValue() != null || !sampleFactorValue.getUnit().getAnnotationValue().isEmpty()) {
                    if (unitIndex == 0) {
                        put("Unit", sampleFactorValue.getUnit().getAnnotationValue());
                    } else {
                        put(unitEntry + unitIndex, sampleFactorValue.getUnit().getAnnotationValue());
                    }
                    /*
                      Unit entry also gets incremented. Unit.1, Unit.2 etc. This has to be correctly tracked and inserted.
                     */
                    /*
                       Unit ontology value is not captured hence introduce empty term source ref and term accession number to pass validation.
                     */
                    put(termSourceReference + index, "");
                    put(termAccessionNumber + index, "");
                    index++;
                    unitIndex++;
                }
            }
        }
        if (sample.getComments().size() > 0) {
            if (sample.getComments().get(0).getValue() != null || !sample.getComments().get(0).getValue().isEmpty()) {
                put(SampleSpreadSheetConstants.ROW_INDEX, sample.getComments().get(0).getValue());
            }

        }
    }
}
