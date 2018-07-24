package uk.ac.ebi.subs.metabolights.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssayDataValidator {

    public static final Logger logger = LoggerFactory.getLogger(AssayDataValidator.class);

    public List<SingleValidationResult> validate(AssayDataValidationMessageEnvelope envelope) {
        List<SingleValidationResult> validationResults = new ArrayList<>();
        return validationResults;
    }

}
