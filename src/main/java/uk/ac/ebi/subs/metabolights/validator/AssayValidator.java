package uk.ac.ebi.subs.metabolights.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.validator.data.AssayValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.*;

@Service
public class AssayValidator {

    public static final Logger logger = LoggerFactory.getLogger(AssayValidator.class);

    public List<SingleValidationResult> validate(AssayValidationMessageEnvelope envelope) {
        List<SingleValidationResult> validationResults = new ArrayList<>();
        Map<String, Collection<Attribute>> factors = getFactorsFrom(envelope.getStudy());
        List<Submittable<Sample>> sampleList = envelope.getSampleList();
        if (factors.isEmpty()) {
            validationResults.add(ValidationUtils.generateSingleValidationResult("No study factors found", SingleValidationResultStatus.Error));
        } else {
            if (sampleList != null && !sampleList.isEmpty()) {
                validationResults.addAll(validateFactorsUseInSamples(factors, sampleList));
            } else {
                validationResults.add(ValidationUtils.generateSingleValidationResult("No samples found in assays", SingleValidationResultStatus.Error));
            }
        }
        return validationResults;
    }


    public Map<String, Collection<Attribute>> getFactorsFrom(Submittable<Study> study) {
        Map<String, Collection<Attribute>> factors = new HashMap<>();
        if (study.getAttributes() != null && !study.getAttributes().isEmpty()) {
            if (study.getAttributes().containsKey("factors")) {
                factors.put("factors", study.getAttributes().get("factors"));
            }
        }
        return factors;
    }


    private List<SingleValidationResult> validateFactorsUseInSamples(Map<String, Collection<Attribute>> factors, List<Submittable<Sample>> sampleList) {
        List<SingleValidationResult> validationResults = new ArrayList<>();
        for (Submittable<Sample> sample : sampleList) {
            Map<String, Collection<Attribute>> sampleFactors = getFactors(sample);
            if (sampleFactors.isEmpty()) {
                validationResults.add(ValidationUtils.generateSingleValidationResult(sample.getAlias() + " has no factors referenced from studies", SingleValidationResultStatus.Error));
            } else {
                for (Attribute studyFactorAttribute : factors.get("factors")) {
                    boolean facotorUsed = false;
                    for (String factorName : sampleFactors.keySet()) {
                        /*
                        The Key in the Sample factor is equal to the Attribute Value in the Study factor collection
                         */
                        if (factorName.equalsIgnoreCase(studyFactorAttribute.getValue())) {
                            Collection<Attribute> attributes = sampleFactors.get(factorName);
                            if (attributes.size() > 0) {
                                Attribute sampleAttribute = attributes.iterator().next();
                                if (sampleAttribute.getValue() != null || !sampleAttribute.getValue().isEmpty()) {
                                    facotorUsed = true;
                                }
                            }
                        }
                    }
                    if (!facotorUsed) {
                        validationResults.add(ValidationUtils.generateSingleValidationResult( "Factor - " + studyFactorAttribute.getValue()  + " - is not used in the sample - " +  sample.getAlias(), SingleValidationResultStatus.Error));
                    }
                }
            }
        }
        return validationResults;
    }


    public Map<String, Collection<Attribute>> getFactors(Submittable<Sample> sample) {
        Map<String, Collection<Attribute>> factors = new HashMap<>();
        if (sample.getAttributes() != null && !sample.getAttributes().isEmpty()) {
            for(Map.Entry<String,Collection<Attribute>> entry : sample.getAttributes().entrySet()){
                if (!entry.getKey().equalsIgnoreCase("Organism") && !entry.getKey().equalsIgnoreCase("Organism part")) {
                    factors.put(entry.getKey(),entry.getValue());
                }
            }
         }
        return factors;
    }


}
