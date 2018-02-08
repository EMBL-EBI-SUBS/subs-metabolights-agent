package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.metabolights.model.*;

import java.util.*;

/**
 * Created by kalai on 11/01/2018.
 */
public class USISampleToMLSample implements Converter<uk.ac.ebi.subs.data.submittable.Sample, Sample> {

    private String ncbiBaseUrl = "http://purl.obolibrary.org/obo/NCBITaxon_";

    @Override
    public Sample convert(uk.ac.ebi.subs.data.submittable.Sample source) {
        Sample sample = new Sample();
        //todo set accession
        //todo set team
        //set name
        sample.setName(source.getAlias());
        // set taxon and title
        SampleSourceModel mlTaxonObject = getTaxonObject();
//        mlTaxonObject.getCharacteristics().get(0).getValue().setTerm_accession(ncbiBaseUrl + source.getTaxonId());
        if (mlTaxonObject.getCharacteristics().get(0).getValue() == null) {
            OntologyModel ontologyModel = new OntologyModel();
            ontologyModel.setTerm_accession(ncbiBaseUrl + source.getTaxonId());
            ontologyModel.setTerm(source.getTaxon());
            mlTaxonObject.getCharacteristics().get(0).setValue(ontologyModel);
        } else {
            mlTaxonObject.getCharacteristics().get(0).getValue().setTerm_accession(ncbiBaseUrl + source.getTaxonId());
        }
        // todo only sample organism is set and not organism part
        mlTaxonObject.setName(source.getTitle());
        sample.setDerives_from(Arrays.asList(mlTaxonObject));
        // todo set description - into comments?
        // todo set release date

        //set attributes (Factor values)
        sample.setFactor_values(convertToSampleAttributes(source.getAttributes()));
        return sample;
    }

    private SampleSourceModel getTaxonObject() {
        SampleSourceModel sampleSourceModel = new SampleSourceModel();
        sampleSourceModel.setCharacteristics(Arrays.asList(new SampleSourceOntologyModel()));
        return sampleSourceModel;
    }

    private List<SampleAttribute> convertToSampleAttributes(Map<String, Collection<Attribute>> usiAttributes) {
        List<SampleAttribute> mlSampleAttributes = new ArrayList<>();
        for (Map.Entry<String, Collection<Attribute>> entry : usiAttributes.entrySet()) {
            //todo clarify collection of attributes for sample factor case
            // use only 1st entry for now.
            if (entry.getValue().size() > 0) {
                Attribute attribute = entry.getValue().iterator().next();
                String url = "";
                if(attribute.getTerms().size() > 0){
                    url = attribute.getTerms().iterator().next().getUrl();
                }

                SampleAttribute sampleAttribute = new SampleAttribute();
                sampleAttribute.setFactor_name(new Factor());
                sampleAttribute.getFactor_name().setFactor_type(new OntologyModel());
                sampleAttribute.setUnit(new OntologyModel());

                sampleAttribute.getFactor_name().getFactor_type().setTerm_accession(url);
                sampleAttribute.getFactor_name().setName(entry.getKey());
                sampleAttribute.setValue(entry.getValue().iterator().next().getValue());
                sampleAttribute.getUnit().setTerm(attribute.getUnits());
                mlSampleAttributes.add(sampleAttribute);
            }
        }
        return mlSampleAttributes;
    }
}
