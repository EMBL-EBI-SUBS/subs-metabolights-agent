package uk.ac.ebi.subs.metabolights.validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.metabolights.converters.WSUtils;
import uk.ac.ebi.subs.validator.data.AssayValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.List;

import static org.junit.Assert.*;

public class AssayValidatorTest {
    
    AssayValidationMessageEnvelope assayValidationMessageEnvelope;
    AssayValidator assayValidator;

    @Before
    public void setUp() throws Exception {
        List<Submittable<Sample>> usiSampleList = WSUtils.getUSISampleListFromDisc();
        Submittable<Study> usiStudy = WSUtils.getUSIStudyFromDisc();
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
       List<SingleValidationResult> validationResults = assayValidator.validate(assayValidationMessageEnvelope);
//        assertEquals(validationResults.size(),0);

        modifySampleList("genotype");
        
        validationResults = assayValidator.validate(assayValidationMessageEnvelope);
        assertEquals(validationResults.size(),16);
        assertEquals(validationResults.get(0).getMessage(), "Factor - genotype - is not used in the sample - Ex1-Col0-48h-Ag-1");

        modifySampleList("technical replicate");

        validationResults = assayValidator.validate(assayValidationMessageEnvelope);
        assertEquals(validationResults.size(),32);


    }

    public void modifySampleList(String key){
        for(Submittable<Sample> sample : assayValidationMessageEnvelope.getSampleList()){
            sample.getAttributes().get(key).clear();
        }
    }
}