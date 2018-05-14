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
        Sample sample = new Sample();
        //todo set accession
        //todo set team
        // todo set release date
        // todo set description - from comments?


        sample.setAlias(source.getName());
        Source mlTaxonObject = extractTaxonObject(source);
        if (mlTaxonObject != null) {
            sample.setTitle(mlTaxonObject.getName());
            if (mlTaxonObject.getCharacteristics().size() > 0) {
                if (mlTaxonObject.getCharacteristics().get(0).getValue() != null) {
                    String ncbi_taxon_url = mlTaxonObject.getCharacteristics().get(0).getValue().getTermAccession();
                    if (ncbi_taxon_url != null && ncbi_taxon_url.contains("NCBITaxon_")) {
                        String[] values = ncbi_taxon_url.split("NCBITaxon_");
                        if (values.length == 2) {
                            sample.setTaxonId(Long.parseLong(values[1]));
                        }
                    }
                    sample.setTaxon(mlTaxonObject.getCharacteristics().get(0).getValue().getAnnotationValue());
                }
            }
        }

        //set factor values and source information into attributes
        Map<String, Collection<Attribute>> usiSampleFactor = convertToUSISampleAttributes(source.getFactorValues());
        Map<String, Collection<Attribute>> usiSampleSource = convertToUSIAttributes(source.getDevivesFrom());
        usiSampleFactor.putAll(usiSampleSource);
        sample.setAttributes(usiSampleFactor);
        return sample;
    }

    private Source extractTaxonObject(uk.ac.ebi.subs.metabolights.model.Sample sample) {
        if(sample.getDevivesFrom() == null){
               return null;
        }
        if (sample.getDevivesFrom().size() > 0) {
            List<Source> derivesFrom = sample.getDevivesFrom();
            for (int i = 0; i < derivesFrom.size(); i++) {
                for (int j = 0; j < derivesFrom.get(i).getCharacteristics().size(); j++) {
                      if(derivesFrom.get(i).getCharacteristics().get(j).getCategory().getAnnotationValue().toLowerCase().equals("organism")){
                           return derivesFrom.get(i);
                      }
                }
            }
        }
        return null;
    }

    private Map<String, Collection<Attribute>> convertToUSISampleAttributes(List<SampleFactorValue> mlSampleFactorValues) {
        Map<String, Collection<Attribute>> usiSampleAttributes = new HashMap<>();
        for (SampleFactorValue factorValue : mlSampleFactorValues) {
            String key = factorValue.getCategory().getFactorName();
            String url = factorValue.getCategory().getFactorType().getTermAccession();
//            String description; todo description is ignored for samples
            String value = factorValue.getValue().getAnnotationValue();

            Attribute attribute = new Attribute();
            attribute.setValue(value);
            Term term = new Term();
            term.setUrl(url);

            if (factorValue.getUnit() != null) {
                attribute.setUnits(factorValue.getUnit().getAnnotationValue());
            }

            attribute.setTerms(Arrays.asList(term));
            usiSampleAttributes.put(key, Arrays.asList(attribute));
        }
        return usiSampleAttributes;
    }


    private Map<String, Collection<Attribute>> convertToUSIAttributes(List<Source> devivesFrom) {
        Map<String, Collection<Attribute>> usiSampleAttributes = new HashMap<>();
        for (Source source : devivesFrom) {
            List<SampleSourceOntologyModel> sourceCharacteristics = source.getCharacteristics();
            for (SampleSourceOntologyModel sourceCharacteristic : sourceCharacteristics) {
                Attribute attribute = new Attribute();
                String key = sourceCharacteristic.getCategory().getAnnotationValue();
                String url = sourceCharacteristic.getValue().getTermAccession();
                String value = sourceCharacteristic.getValue().getAnnotationValue();
                attribute.setValue(value);
                Term term = new Term();
                term.setUrl(url);
                attribute.setTerms(Arrays.asList(term));
                if (sourceCharacteristic.getUnit() != null) {
                   attribute.setUnits(sourceCharacteristic.getUnit().getAnnotationValue());
                }
                usiSampleAttributes.put(key,Arrays.asList(attribute));
            }

        }
        return usiSampleAttributes;
    }

}
