package uk.ac.ebi.subs.metabolights.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class ProtocolValidator {
    public static final Logger logger = LoggerFactory.getLogger(ProtocolValidator.class);

    public List<SingleValidationResult> validateContent(List<Protocol> protocols) {
        List<SingleValidationResult> protocolValidations = new ArrayList<>();
        if (protocols != null && !protocols.isEmpty()) {
            for (Protocol protocol : protocols) {
                String protocolName = protocol.getTitle();
                if (protocol.getDescription() == null || protocol.getDescription().isEmpty()) {
                    protocolValidations.add(ValidationUtils.generateSingleValidationResult(protocol, "Protocol " +
                            protocolName +
                            " has no description provided", SingleValidationResultStatus.Error));
                } else {
                    if (!ValidationUtils.minCharRequirementPassed(protocol.getDescription(), 3)) {
                        protocolValidations.add(ValidationUtils.generateSingleValidationResult(protocol, "Protocol " +
                                protocolName +
                                " description is not sufficient", SingleValidationResultStatus.Error));
                    }
                }
            }
        }
        return protocolValidations;
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
        if(technologyType != null){

        }   else{
              requiredFieldsValidation.add(ValidationUtils.generateSingleValidationResult(assay,
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
}