package uk.ac.ebi.subs.metabolights.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.StudyDataType;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.*;

@Service
public class ProtocolValidator {
    public static final Logger logger = LoggerFactory.getLogger(ProtocolValidator.class);


    public List<SingleValidationResult> validate(List<Submittable<Protocol>> protocols, StudyDataType studyDataType) {

        List<SingleValidationResult> protocolValidations = new ArrayList<>();
        if (protocols != null && !protocols.isEmpty()) {
            protocolValidations.addAll(validateContent(protocols));
            protocolValidations.addAll(validateRequiredFields(protocols, studyDataType));
        } else {
            protocolValidations.add(ValidationUtils.generateSingleValidationResult("No study protocols provided", SingleValidationResultStatus.Error));

        }
        return protocolValidations;
    }


    public List<SingleValidationResult> validateContent(List<Submittable<Protocol>> protocols) {
        List<SingleValidationResult> protocolContentValidations = new ArrayList<>();

        for (Submittable<Protocol> protocol : protocols) {
            String protocolName = protocol.getTitle();
            if (protocol.getDescription() == null || protocol.getDescription().isEmpty()) {
                protocolContentValidations.add(ValidationUtils.generateSingleValidationResult(protocol, "Protocol " +
                        protocolName +
                        " has no description provided", SingleValidationResultStatus.Error));
            } else {
                if (!ValidationUtils.minCharRequirementPassed(protocol.getDescription(), 3)) {
                    protocolContentValidations.add(ValidationUtils.generateSingleValidationResult(protocol, "Protocol " +
                            protocolName +
                            " description is not sufficient", SingleValidationResultStatus.Error));
                }
            }
        }
        return protocolContentValidations;
    }

    /*
     *   Assay object hold the information about technologyType in its attributes. The technologyType
     *   is required to validate the necessary protocol fields that has to be present for the given
     *   study.
     *
     * */
    public List<SingleValidationResult> validateRequiredFields(List<Submittable<Protocol>> protocols, StudyDataType studyDataType) {
        List<SingleValidationResult> requiredFieldsValidation = new ArrayList<>();
        if (studyDataType != null) {
            requiredFieldsValidation.addAll(validateBasedOn(studyDataType, protocols));
            return requiredFieldsValidation;
        } else {
            requiredFieldsValidation.add(ValidationUtils.generateSingleValidationResult(
                    "Study has no study technologyType information", SingleValidationResultStatus.Error));
        }
        return requiredFieldsValidation;
    }

    private List<SingleValidationResult> validateBasedOn(StudyDataType studyDataType, List<Submittable<Protocol>> protocols) {
        List<SingleValidationResult> validations = new ArrayList<>();
        List<String> requiredProtocolFields = getRequiredProtocolFieldsFor(studyDataType, protocols);

        for (String expectedField : requiredProtocolFields) {
            boolean isPresent = true;
            for (Submittable<Protocol> protocol : protocols) {
                isPresent = isProtocolPresent(expectedField, protocol);
                if(isPresent){
                   break;
                }
            }
            if(!isPresent){
                 validations.add(ValidationUtils.generateSingleValidationResult(expectedField + " protocol is not " +
                         "present in the study protocols", SingleValidationResultStatus.Error )) ;
            }
        }
        return validations;
    }

    private List<String> getRequiredProtocolFieldsFor(StudyDataType studyDataType, List<Submittable<Protocol>> protocols) {
        List<String> requiredProtocolFields = new ArrayList<>();
        boolean isImagingStudy = isImagingStudy(protocols);
        requiredProtocolFields.addAll(getCommonFields(isImagingStudy));
        if (isMS(studyDataType)) {
            requiredProtocolFields.addAll(getMSSpecificFields(isImagingStudy));

        } else if (isNMR(studyDataType)) {
            requiredProtocolFields.addAll(getNMRSpecificFields(isImagingStudy));
        }
        return requiredProtocolFields;
    }

    private List<String> getCommonFields(boolean isImagingStudy) {
        List<String> requiredProtocolFields = new ArrayList<>();
        requiredProtocolFields.add("Data transformation");
        requiredProtocolFields.add("Metabolite identification");
        if (!isImagingStudy) {
            requiredProtocolFields.add("Extraction");
        }
        return requiredProtocolFields;
    }

    private boolean isImagingStudy(List<Submittable<Protocol>> protocols) {
        for (Submittable<Protocol> protocol : protocols) {
            if (protocol.getTitle().equalsIgnoreCase("Magnetic resonance imaging")) {
                return true;
            }
            if (protocol.getTitle().equalsIgnoreCase("Histology")) {
                return true;
            }
        }
        return false;
    }

    private boolean isMS(StudyDataType studyDataType) {
        return studyDataType.name().equalsIgnoreCase("Metabolomics_MS");
    }

    private boolean isNMR(StudyDataType studyDataType) {
        return studyDataType.name().equalsIgnoreCase("Metabolomics_NMR");
    }

    private List<String> getMSSpecificFields(boolean isImagingStudy) {
        List<String> requiredProtocolFields = new ArrayList<>();
        if (isImagingStudy) {
            requiredProtocolFields.add("Histology");
            requiredProtocolFields.add("Preparation");
        } else {
            requiredProtocolFields.add("Chromatography");
            requiredProtocolFields.add("Mass spectrometry");
        }
        return requiredProtocolFields;
    }

    private List<String> getNMRSpecificFields(boolean isImagingStudy) {
        List<String> requiredProtocolFields = new ArrayList<>();
        if (isImagingStudy) {
            requiredProtocolFields.add("Magnetic resonance imaging");
            requiredProtocolFields.add("In vivo magnetic resonance spectroscopy");
            requiredProtocolFields.add("In vivo magnetic resonance assay");
        } else {
            requiredProtocolFields.add("NMR sample");
            requiredProtocolFields.add("NMR spectroscopy");
            requiredProtocolFields.add("NMR assay");
        }
        return requiredProtocolFields;
    }

    public boolean isProtocolPresent(String requiredProtocolField, Submittable<Protocol> protocol) {
        return requiredProtocolField.equalsIgnoreCase(protocol.getTitle());
    }
}