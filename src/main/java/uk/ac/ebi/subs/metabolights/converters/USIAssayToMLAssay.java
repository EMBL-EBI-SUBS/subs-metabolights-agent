package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Term;
import uk.ac.ebi.subs.metabolights.model.Assay;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Created by kalai on 29/01/2018.
 */
public class USIAssayToMLAssay implements Converter<uk.ac.ebi.subs.data.submittable.Assay, Assay> {
    @Override
    public Assay convert(uk.ac.ebi.subs.data.submittable.Assay source) {
        Assay assay = new Assay();
        assay.setFilename(source.getAlias());

        Map<String, Collection<Attribute>> usiAssayAttributes = source.getAttributes();

        for (Map.Entry<String, Collection<Attribute>> entry : usiAssayAttributes.entrySet()) {
            Attribute attribute = entry.getValue().iterator().next();

            if (entry.getKey().toLowerCase().equals("technologytype")) {
                if (attribute != null) {
                    assay.setTechnologyType(new OntologyModel());
                    assay.getTechnologyType().setAnnotationValue(attribute.getValue());
                    assay.getTechnologyType().setTermAccession(attribute.getTerms().get(0).getUrl());
                }
            }
            if (entry.getKey().toLowerCase().equals("measurementType")) {
                if (attribute != null) {
                    assay.setMeasurementType(new OntologyModel());
                    assay.getMeasurementType().setAnnotationValue(attribute.getValue());
                    assay.getMeasurementType().setTermAccession(attribute.getTerms().get(0).getUrl());
                }
            }
            if (entry.getKey().toLowerCase().equals("technologyPlatform")) {
                assay.setTechnologyPlatform(attribute.getValue());
            }

            assay.setDataFiles(new ArrayList<>());
        }
        return assay;
    }
}
