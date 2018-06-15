package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

public class USIFactorToMLFactor implements Converter<Attribute, Factor> {
    @Override
    public Factor convert(Attribute source) {
        Factor factor = new Factor();
        factor.setFactorType(new OntologyModel());
        factor.getFactorType().setAnnotationValue(source.getValue());
        if(source.getTerms()!=null){
             if(source.getTerms().size() > 0){
               factor.getFactorType().setTermAccession(source.getTerms().get(0).getUrl());
             }
        }
        return factor;
    }
}
