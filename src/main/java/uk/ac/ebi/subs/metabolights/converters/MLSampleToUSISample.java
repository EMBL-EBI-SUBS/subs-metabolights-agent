package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Term;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.metabolights.model.*;

import java.util.*;


/**
 * Created by kalai on 11/01/2018.
 */
public class MLSampleToUSISample implements Converter<uk.ac.ebi.subs.metabolights.model.Sample, Sample> {
    private String ncbiBaseUrl = "http://purl.obolibrary.org/obo/NCBITaxon_";

    @Override
    public Sample convert(uk.ac.ebi.subs.metabolights.model.Sample source) {
        //  uk.ac.ebi.subs.metabolights.model.Sample sample = new uk.ac.ebi.subs.metabolights.model.Sample();
        Sample sample = new Sample();
        //todo set accession
        //todo set team
        //set name
        sample.setAlias(source.getName());
        SampleSourceModel mlTaxonObject = extractTaxonObject(source);
        if (mlTaxonObject != null) {
            sample.setTitle(mlTaxonObject.getName());
            if (mlTaxonObject.getCharacteristics().size() > 0) {
                if (mlTaxonObject.getCharacteristics().get(0).getValue() != null) {
                    String ncbi_taxon_url = mlTaxonObject.getCharacteristics().get(0).getValue().getTerm_accession();
                    if (ncbi_taxon_url != null && ncbi_taxon_url.contains("NCBITaxon_")) {
                        String[] values = ncbi_taxon_url.split("NCBITaxon_");
                        if (values.length == 2) {
                            sample.setTaxonId(Long.parseLong(values[1]));
                        }
                    }
                    sample.setTaxon(mlTaxonObject.getCharacteristics().get(0).getValue().getTerm());
                }
            }
        }
        // todo set description - from comments?
        // todo set release date

        //set attributes (Factor values)
        sample.setAttributes(convertToUSISampleAttributes(source.getFactor_values()));
        return sample;
    }

    private SampleSourceModel extractTaxonObject(uk.ac.ebi.subs.metabolights.model.Sample source) {
        //todo the order of return might not always be the same
        //todo resolve adding organism_part
        if (source.getDerives_from().size() > 0) {
            SampleSourceModel mlTaxonObject = source.getDerives_from().get(0);
            return mlTaxonObject;
        }
        return null;
    }

    private Map<String, Collection<Attribute>> convertToUSISampleAttributes(List<SampleAttribute> mlSampleAttributes) {
        Map<String, Collection<Attribute>> usiSampleAttributes = new HashMap<>();
        for (SampleAttribute mlSampleAttribute : mlSampleAttributes) {
            String key = mlSampleAttribute.getFactor_name().getName();
            String url = mlSampleAttribute.getFactor_name().getFactor_type().getTerm_accession();
            String description = mlSampleAttribute.getValue();



            Attribute attribute = new Attribute();
            attribute.setValue(description);
            Term term = new Term();
            term.setUrl(url);

            if(mlSampleAttribute.getUnit() != null){
               attribute.setUnits(mlSampleAttribute.getUnit().getTerm());
            }

            attribute.setTerms(Arrays.asList(term));
            usiSampleAttributes.put(key, Arrays.asList(attribute));
        }
        return usiSampleAttributes;
    }
}
