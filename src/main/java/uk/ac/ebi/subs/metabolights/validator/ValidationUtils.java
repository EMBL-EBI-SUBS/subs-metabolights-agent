package uk.ac.ebi.subs.metabolights.validator;

import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.List;

public class ValidationUtils {

    public static SingleValidationResult generateSingleValidationResult(Submittable submittable, String message, SingleValidationResultStatus status) {
        SingleValidationResult result = new SingleValidationResult();
        result.setEntityUuid(submittable.getId());
        result.setMessage(message);
        result.setValidationAuthor(ValidationAuthor.Metabolights);
        result.setValidationStatus(status);
        return result;
    }

    public static SingleValidationResultsEnvelope buildSingleValidationResultsEnvelope(List<SingleValidationResult> validationResults, int validationResultVersion, String validationResultUUID) {
        return new SingleValidationResultsEnvelope(
                validationResults, validationResultVersion, validationResultUUID, ValidationAuthor.Metabolights
        );
    }

    public static boolean hasValidationError(List<SingleValidationResult> validationResults) {
        SingleValidationResult errorValidationResult = validationResults.stream().filter(
                validationResult -> validationResult.getValidationStatus() == SingleValidationResultStatus.Error)
                .findAny()
                .orElse(null);

        return errorValidationResult != null;
    }
}
