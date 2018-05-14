package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Term;
import uk.ac.ebi.subs.metabolights.model.Factor;

public class MLFactorToUSIFactor implements Converter<Factor, Attribute> {
    @Override
    public Attribute convert(Factor source) {
        Attribute attribute = new Attribute();
        if (source.getFactorType() != null) {
            Term term = new Term();
            term.setUrl(source.getFactorType().getTermAccession());
            attribute.getTerms().add(term);
            attribute.setValue(source.getFactorType().getAnnotationValue());
        }
        return attribute;
    }
}
