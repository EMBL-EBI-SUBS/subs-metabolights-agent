package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

import java.util.ArrayList;

@Service
public class USIFactorToMLFactor implements Converter<Attribute, Factor> {
    @Override
    public Factor convert(Attribute source) {
        Factor factor = new Factor();
        factor.setFactorType(new OntologyModel());
        factor.getFactorType().setAnnotationValue(source.getValue());
        factor.setFactorName(source.getValue());
        factor.getFactorType().setComments(new ArrayList<>());
        //todo decide if factor name should be derived from ontology term
        // ideally the factor name must be set by querying the ontology URL
        if(source.getTerms()!=null){
             if(source.getTerms().size() > 0){
               factor.getFactorType().setTermAccession(source.getTerms().get(0).getUrl());
             }
        }
        factor.setComments(new ArrayList<>());
        return factor;
    }
}
