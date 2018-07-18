package uk.ac.ebi.subs.metabolights.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.StudyDataType;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyValidator {
    public static final Logger logger = LoggerFactory.getLogger(StudyValidator.class);
    @Autowired
    private ProtocolValidator protocolValidator;


    public List<SingleValidationResult> validate(StudyValidationMessageEnvelope envelope) {
        List<SingleValidationResult> validationResults = new ArrayList<>();

        validationResults.addAll(validateContacts(envelope.getProject().getBaseSubmittable()));
        validationResults.addAll(validateProtocols(envelope.getProtocols(),envelope.getEntityToValidate().getStudyType()));
        //todo validate publications
        return validationResults;
    }

    public List<SingleValidationResult> validateContacts(Project project){
        List<SingleValidationResult>  contactValidations = new ArrayList<>();
        if(project.getContacts()!=null){
          for(Contact contact : project.getContacts()){
             if(contact.getEmail() == null || contact.getEmail().isEmpty()){
                 contactValidations.add(ValidationUtils.generateSingleValidationResult(project, "Contact: " +
                         contact.getFirstName() + " " + contact.getLastName() +
                         " has no associated email", SingleValidationResultStatus.Error));
             }
          }
       }
       return contactValidations;
    }

    public List<SingleValidationResult> validateProtocols(List<Protocol> protocols, StudyDataType studyType){
        return protocolValidator.validate(protocols,studyType);
    }

}
