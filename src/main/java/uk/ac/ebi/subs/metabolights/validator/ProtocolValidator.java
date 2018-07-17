package uk.ac.ebi.subs.metabolights.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProtocolValidator {
    public static final Logger logger = LoggerFactory.getLogger(ProtocolValidator.class);

    public List<SingleValidationResult> validateProtocols(List<Protocol> protocols){
        List<SingleValidationResult>  protocolValidations = new ArrayList<>();
        if(protocols!= null && !protocols.isEmpty()){
            for(Protocol protocol : protocols){
                String protocolName = protocol.getTitle();
                if(protocol.getDescription() == null || protocol.getDescription().isEmpty()){
                    protocolValidations.add(ValidationUtils.generateSingleValidationResult(protocol, "Protocol " +
                            protocolName +
                            " has no description provided", SingleValidationResultStatus.Error));
                }  else{
                    if(!ValidationUtils.minCharRequirementPassed(protocol.getDescription(),3)){
                        protocolValidations.add(ValidationUtils.generateSingleValidationResult(protocol, "Protocol " +
                                protocolName +
                                " description is not sufficient", SingleValidationResultStatus.Error));
                    }
                }
            }
        }
        return protocolValidations;
    }
}
