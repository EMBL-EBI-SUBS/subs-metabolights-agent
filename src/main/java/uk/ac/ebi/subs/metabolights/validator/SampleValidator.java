package uk.ac.ebi.subs.metabolights.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.metabolights.validator.schema.JsonSchemaValidationHandler;
import uk.ac.ebi.subs.metabolights.validator.schema.JsonSchemaValidationService;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;

import java.util.ArrayList;
import java.util.List;

@Service
public class SampleValidator {

    public static final Logger logger = LoggerFactory.getLogger(StudyValidator.class);
    @Autowired
    private JsonSchemaValidationHandler jsonSchemaValidationHandler;


    public List<SingleValidationResult> validate(SampleValidationMessageEnvelope envelope) {
        List<SingleValidationResult> validationResults = jsonSchemaValidationHandler.handleSampleValidation(envelope);
        return validationResults;
    }
}
