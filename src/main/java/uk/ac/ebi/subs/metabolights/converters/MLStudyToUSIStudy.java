package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;
import uk.ac.ebi.subs.metabolights.model.Study;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalai on 18/12/2017.
 */
public class MLStudyToUSIStudy  implements Converter<Study, uk.ac.ebi.subs.data.submittable.Study> {

    MLFactorToUSIFactor mlFactorToUSIFactor = new MLFactorToUSIFactor();
    MLDescriptorToUSIDescriptor mlDescriptorToUSIDescriptor = new MLDescriptorToUSIDescriptor();

    @Override
    public uk.ac.ebi.subs.data.submittable.Study convert(Study source) {
        uk.ac.ebi.subs.data.submittable.Study usiStudy = new uk.ac.ebi.subs.data.submittable.Study();

        usiStudy.setTitle(source.getTitle());
        usiStudy.setAccession(source.getIdentifier());
        usiStudy.setDescription(source.getDescription());

        usiStudy.getAttributes().put("studyDesignDescriptors", convertStudyDescriptors(source.getStudyDesignDescriptors()));
        usiStudy.getAttributes().put("factors", convertSampleFactors(source.getFactors()));

        return usiStudy;
    }


    private List<Attribute> convertSampleFactors(List<Factor> factors) {
        List<Attribute> sampleFactors = new ArrayList<>();
        for (Factor factor : factors) {
            sampleFactors.add(mlFactorToUSIFactor.convert(factor));
        }
        return sampleFactors;
    }


    private List<Attribute> convertStudyDescriptors(List<OntologyModel> studyDesignDescriptors) {
        List<Attribute> descriptors = new ArrayList<>();
        for (OntologyModel descriptor : studyDesignDescriptors) {
            descriptors.add(mlDescriptorToUSIDescriptor.convert(descriptor));
        }
        return descriptors;
    }
}
