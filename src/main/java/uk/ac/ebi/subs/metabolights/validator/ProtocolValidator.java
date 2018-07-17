package uk.ac.ebi.subs.metabolights.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class ProtocolValidator {
    public static final Logger logger = LoggerFactory.getLogger(ProtocolValidator.class);


    public List<SingleValidationResult> validate(List<Protocol> protocols, Assay assay) {

        List<SingleValidationResult> protocolValidations = new ArrayList<>();
        if (protocols != null && !protocols.isEmpty()) {
            protocolValidations.addAll(validateContent(protocols));
            protocolValidations.addAll(validateRequiredFields(protocols, assay));
        } else {
            protocolValidations.add(ValidationUtils.generateSingleValidationResult("No study protocols provided", SingleValidationResultStatus.Error));

        }
        return protocolValidations;
    }


    public List<SingleValidationResult> validateContent(List<Protocol> protocols) {
        List<SingleValidationResult> protocolContentValidations = new ArrayList<>();

        for (Protocol protocol : protocols) {
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
    public List<SingleValidationResult> validateRequiredFields(List<Protocol> protocols, Assay assay) {
        List<SingleValidationResult> requiredFieldsValidation = new ArrayList<>();
        String technologyType = getTechnologyType(assay);
        if (technologyType != null) {

        } else {
            requiredFieldsValidation.add(ValidationUtils.generateSingleValidationResult(
                    "Assay has no technologyType information", SingleValidationResultStatus.Error));
        }
        return requiredFieldsValidation;
    }

    private String getTechnologyType(Assay assay) {
        if (assay != null) {
            if (assay.getAttributes() != null && assay.getAttributes().size() > 0) {
                for (Map.Entry<String, Collection<Attribute>> entry : assay.getAttributes().entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("technologyType")) {
                        if (entry.getValue() != null && entry.getValue().size() > 0) {
                            return entry.getValue().iterator().next().getValue();
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<SingleValidationResult> validateBasedOn(String technologyType, List<Protocol> protocols) {
        List<SingleValidationResult> validation = new ArrayList<>();
        List<String> requiredProtocolFields = new ArrayList<>();


        return validation;
    }

    private boolean isImagingStudy(List<Protocol> protocols) {
        for (Protocol protocol : protocols) {
            if (protocol.getTitle().equalsIgnoreCase("Magnetic resonance imaging")) {
                return true;
            }
            if (protocol.getTitle().equalsIgnoreCase("Histology")) {
                return true;
            }
        }
        return false;
    }

    private boolean isMS(String technologyType) {
        return technologyType.equalsIgnoreCase("mass spectrometry");
    }

    private boolean isNMR(String technologyType) {
        return technologyType.equalsIgnoreCase("NMR spectroscopy");
    }

    private List<String> getRequiredProtocolFieldsFor(String technologyType, List<Protocol> protocols) {
        List<String> requiredProtocolFields = new ArrayList<>();
        boolean isImagingStudy = isImagingStudy(protocols);
        requiredProtocolFields.addAll(getCommonFields(isImagingStudy));
        if (isMS(technologyType)) {
            requiredProtocolFields.addAll(getMSSpecificFields(isImagingStudy));

        } else if (isNMR(technologyType)) {
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

    private List<String> getMSSpecificFields(boolean isImagingStudy) {
        List<String> requiredProtocolFields = new ArrayList<>();
        if (isImagingStudy) {
            requiredProtocolFields.add("Histology");
            requiredProtocolFields.add("Preparation");
        } else {
            requiredProtocolFields.add("Chromatography");
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
}