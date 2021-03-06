package uk.ac.ebi.subs.metabolights.validator;

import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.metabolights.model.StudyAttributes;
import uk.ac.ebi.subs.metabolights.model.StudyDataType;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ValidationUtils {

    public static SingleValidationResult generateSingleValidationResult(Submittable submittable, String message, SingleValidationResultStatus status) {
        SingleValidationResult result = new SingleValidationResult();
        result.setEntityUuid(submittable.getId());
        result.setMessage(message);
        result.setValidationAuthor(ValidationAuthor.Metabolights);
        result.setValidationStatus(status);
        return result;
    }

    public static SingleValidationResult generateSingleValidationResult(String message, SingleValidationResultStatus status) {
        SingleValidationResult result = new SingleValidationResult();
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

    public static List<SingleValidationResult> getSinglePassResultIfNoErrors(List<SingleValidationResult> validatedResults){
        if(validatedResults.size() == 0){
            SingleValidationResult singleValidationResult = new SingleValidationResult();
            singleValidationResult.setMessage("PASS");
            singleValidationResult.setValidationAuthor(ValidationAuthor.Metabolights);
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Pass);
            List<SingleValidationResult> results = new ArrayList<>();
            results.add(singleValidationResult);
            return results;
        }
        return validatedResults;
    }

    public static boolean minCharRequirementPassed(String toCheck, int limit) {

        // Test for null values
        if (toCheck == null) return false;
        return toCheck.length() >= limit;
    }


    public static boolean statusIsNotPassOrPending(SingleValidationResult r) {

        return !(r.getValidationStatus().equals(SingleValidationResultStatus.Pass)
                || r.getValidationStatus().equals(SingleValidationResultStatus.Pending));
    }

    public static SingleValidationResultsEnvelope generateSingleValidationResultsEnvelope(
            int validationResultVersion, String validationResultUUID, List<SingleValidationResult> singleValidationResults,
            ValidationAuthor validationAuthor) {

        return new SingleValidationResultsEnvelope(
                singleValidationResults,
                validationResultVersion,
                validationResultUUID,
                validationAuthor
        );
    }

    public static SingleValidationResult generatePassingSingleValidationResult(String entityUuid, ValidationAuthor author) {
        SingleValidationResult validationResult = new SingleValidationResult();
        validationResult.setValidationAuthor(author);
        validationResult.setEntityUuid(entityUuid);

        validationResult.setValidationStatus(SingleValidationResultStatus.Pass);
        return validationResult;
    }

    public static StudyDataType getStudyDataType(Study study){
        Map<String, Collection<Attribute>> attributes = study.getAttributes();
        if(attributes.containsKey(StudyAttributes.STUDY_TYPE)){
           for(StudyDataType studyDataType : StudyDataType.values()){
               if(attributes.get(StudyAttributes.STUDY_TYPE).iterator().next().getValue().equals(studyDataType.name())){
                   return studyDataType;
               }
           }
        }
        return null;
    }
}
