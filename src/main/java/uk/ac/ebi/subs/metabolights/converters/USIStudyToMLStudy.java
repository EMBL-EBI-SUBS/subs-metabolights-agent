package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;
import uk.ac.ebi.subs.metabolights.model.Study;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by kalai on 01/06/2018.
 */

public class USIStudyToMLStudy implements Converter<uk.ac.ebi.subs.data.submittable.Study, Study> {
    USIFactorToMLFactor usiFactorToMLFactor = new USIFactorToMLFactor();
    USIDescriptorToMLDescriptor usiDescriptorToMLDescriptor = new USIDescriptorToMLDescriptor();

    @Override
    public Study convert(uk.ac.ebi.subs.data.submittable.Study source) {
        Study mlStudy = new Study();
        mlStudy.setIdentifier(source.getAccession());
        mlStudy.setTitle(source.getTitle());
        mlStudy.setDescription(source.getDescription());

        mlStudy.setFactors(convertFactors(source.getAttributes()));
        mlStudy.setStudyDesignDescriptors(convertDescriptors(source.getAttributes()));

        return mlStudy;
    }

    private List<OntologyModel> convertDescriptors(Map<String, Collection<Attribute>> attributes) {
        List<OntologyModel> studyDesignDescriptors = new ArrayList<>();
        for (Map.Entry<String, Collection<Attribute>> entry : attributes.entrySet()) {
            if (entry.getKey().equals("studyDesignDescriptors")) {
                for (Attribute attribute : entry.getValue()) {
                    studyDesignDescriptors.add(usiDescriptorToMLDescriptor.convert(attribute));
                }
                return studyDesignDescriptors;
            }
        }
        return studyDesignDescriptors;
    }

    private List<Factor> convertFactors(Map<String, Collection<Attribute>> attributes) {
        List<Factor> factors = new ArrayList<>();
        for (Map.Entry<String, Collection<Attribute>> entry : attributes.entrySet()) {
            if (entry.getKey().equals("factors")) {
                for (Attribute attribute : entry.getValue()) {
                    factors.add(usiFactorToMLFactor.convert(attribute));
                }
                return factors;
            }
        }
        return factors;
    }
}
