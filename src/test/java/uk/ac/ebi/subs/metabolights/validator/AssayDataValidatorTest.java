package uk.ac.ebi.subs.metabolights.validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.util.List;

import static org.junit.Assert.*;

public class AssayDataValidatorTest {

    AssayDataValidator assayDataValidator;
    AssayDataValidationMessageEnvelope assayDataValidationMessageEnvelope;

    @Before
    public void setUp() throws Exception {
        this.assayDataValidator = new AssayDataValidator();
        assayDataValidationMessageEnvelope = new AssayDataValidationMessageEnvelope();
        assayDataValidationMessageEnvelope.setEntityToValidate(ValidationTestUtils.getAssayData());
        assayDataValidationMessageEnvelope.setProtocols(ValidationTestUtils.getProtocols());
    }

    @After
    public void tearDown() throws Exception {
        this.assayDataValidator = null;
        this.assayDataValidationMessageEnvelope = null;
    }


    @Test
    public void validateMAF() {
        List<SingleValidationResult> validationResults = assayDataValidator.validateMAF(assayDataValidationMessageEnvelope.getEntityToValidate().getFiles(), assayDataValidationMessageEnvelope.getProtocols());
        assertEquals(validationResults.size(),0);
    }

    @Test
    public void hasValidFileNamePattern() {
        List<SingleValidationResult> validationResults = assayDataValidator.hasValidFileNamePattern(assayDataValidationMessageEnvelope.getEntityToValidate().getFiles().get(0));
        assertEquals(validationResults.size(),0);
    }

    @Test
    public void hasMafThenValidateCorrespondingProtocol() {
        List<SingleValidationResult> validationResults = assayDataValidator.hasMafThenValidateCorrespondingProtocol(assayDataValidationMessageEnvelope.getEntityToValidate().getFiles(), assayDataValidationMessageEnvelope.getProtocols());
        assertEquals(validationResults.size(),0);
    }
}