package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Term;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

import java.util.Arrays;

public class MLDescriptorToUSIDescriptor implements Converter<OntologyModel, Attribute> {
    @Override
    public Attribute convert(OntologyModel source) {
        Attribute attribute = new Attribute();
        attribute.setValue(source.getAnnotationValue());
        if(source.getTermAccession()!=null && !source.getTermAccession().isEmpty()){
            Term term = new Term();
            term.setUrl(source.getTermAccession());
            attribute.setTerms(Arrays.asList(term));
        }
        return attribute;
    }
}
