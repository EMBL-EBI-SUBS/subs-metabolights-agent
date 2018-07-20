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
                    /*
                    compare annotation value with attribute value
                     */
                    for (String factor : sampleFactors.keySet()) {
                        Collection<Attribute> attributes = sampleFactors.get(factor);
                        for (Attribute sampleFactorAttribute : attributes) {
                            if (sampleFactorAttribute.getValue() != null || !sampleFactorAttribute.getValue().isEmpty()) {
                                if(sampleFactorAttribute.getValue().equalsIgnoreCase(studyFactorAttribute.getValue())){
                                    facotorUsed = true;
                                    break;
                                }
                            }
                        }
                        if(facotorUsed) break;
                    }
                    if(!facotorUsed){
                        validationResults.add(ValidationUtils.generateSingleValidationResult(studyFactorAttribute + " factor is not used in the samples" , SingleValidationResultStatus.Error));
                    }
                }
            }
        }
        return validationResults;
    }


    public Map<String, Collection<Attribute>> getFactors(Submittable<Sample> sample) {
        Map<String, Collection<Attribute>> factors = new HashMap<>();
        if (sample.getAttributes() != null && !sample.getAttributes().isEmpty()) {
            sample.getAttributes().forEach((key, value) -> {
                if (!key.equalsIgnoreCase("Organism") || !key.equalsIgnoreCase("Organism part")) {
                    factors.put(key, value);
                }
            });
        }
        return factors;
    }


}
