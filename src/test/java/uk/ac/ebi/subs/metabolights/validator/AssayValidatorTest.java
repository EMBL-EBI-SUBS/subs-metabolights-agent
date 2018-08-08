package uk.ac.ebi.subs.metabolights.validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.metabolights.converters.Utilities;
import uk.ac.ebi.subs.metabolights.converters.WSUtils;
import uk.ac.ebi.subs.validator.data.AssayValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AssayValidatorTest {

    AssayValidationMessageEnvelope assayValidationMessageEnvelope;
    AssayValidator assayValidator;

    @Before
    public void setUp() throws Exception {
        List<Submittable<Sample>> usiSampleList = Utilities.getUSISampleListFromDisc();
        Submittable<Study> usiStudy = Utilities.getUSIStudyFromDisc();
        assayValidationMessageEnvelope = new AssayValidationMessageEnvelope();
        assayValidationMessageEnvelope.setStudy(usiStudy);
        assayValidationMessageEnvelope.setSampleList(usiSampleList);
        assayValidator = new AssayValidator();
    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void validate() {
        validateAssay();
        validateEmptyFactorValues();
        validateSampleMissingEntries();
        validateStudyFactors();
        validateEmptySampleList();
    }

    private void modifySampleList(String key) {
        for (Submittable<Sample> sample : assayValidationMessageEnvelope.getSampleList()) {
            sample.getAttributes().get(key).clear();
        }
    }

    private void resetSampleList() {
        this.assayValidationMessageEnvelope.setSampleList(Utilities.getUSISampleListFromDisc());
    }

    private void removeEntryFromSampleList(String key) {
        for (Submittable<Sample> sample : assayValidationMessageEnvelope.getSampleList()) {
            sample.getAttributes().remove(key);
        }
    }

    private void removeStudyFactors() {
        this.assayValidationMessageEnvelope.getStudy().getAttributes().remove("factors");
    }

    private void removeSampleList() {
        this.assayValidationMessageEnvelope.setSampleList(new ArrayList<>());
    }

    private void validateAssay() {
        List<SingleValidationResult> validationResults = assayValidator.validate(assayValidationMessageEnvelope);
        assertEquals(validationResults.size(), 0);
    }

    private void validateEmptyFactorValues() {
        modifySampleList("genotype");

        List<SingleValidationResult> validationResults = assayValidator.validate(assayValidationMessageEnvelope);
        assertEquals(validationResults.size(), 16);
        assertEquals(validationResults.get(0).getMessage(), "Factor - genotype - is used in the sample - Ex1-Col0-48h-Ag-1 - but has no value provided");

        modifySampleList("technical replicate");

        validationResults = assayValidator.validate(assayValidationMessageEnvelope);
        assertEquals(validationResults.size(), 32);
    }

    private void validateStudyFactors() {
        removeStudyFactors();
        List<SingleValidationResult> validationResults = assayValidator.validate(assayValidationMessageEnvelope);
        assertEquals(validationResults.size(), 1);
        assertEquals(validationResults.get(0).getMessage(), "No study factors found in the Study");
    }

    private void validateEmptySampleList() {
        removeSampleList();
        List<SingleValidationResult> validationResults = assayValidator.validate(assayValidationMessageEnvelope);
        assertEquals(validationResults.size(), 2);
        assertEquals(validationResults.get(1).getMessage(), "No samples found in assays");
    }

    private void validateSampleMissingEntries() {
        resetSampleList();

        removeEntryFromSampleList("genotype");
        List<SingleValidationResult> validationResults = assayValidator.validate(assayValidationMessageEnvelope);
        assertEquals(validationResults.size(), 16);
        assertEquals(validationResults.get(0).getMessage(), "Factor - genotype - is not used in the sample - Ex1-Col0-48h-Ag-1");

        removeEntryFromSampleList("technical replicate");
        validationResults = assayValidator.validate(assayValidationMessageEnvelope);
        assertEquals(validationResults.size(), 16);
        assertEquals(validationResults.get(0).getMessage(), "Sample - Ex1-Col0-48h-Ag-1 - has no reference to the factors given in the Study");

    }


}