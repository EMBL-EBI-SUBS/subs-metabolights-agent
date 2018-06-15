package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

public class USIDescriptorToMLDescriptor implements Converter<Attribute, OntologyModel> {
    @Override
    public OntologyModel convert(Attribute source) {
        OntologyModel descriptor = new OntologyModel();
        descriptor.setAnnotationValue(source.getValue());
        if (source.getTerms() != null) {
            if (source.getTerms().size() > 0) {
                descriptor.setTermAccession(source.getTerms().get(0).getUrl());
            }
        }
        return descriptor;
    }
}
