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
                    //todo set taxon object by checking the organism name attribute
                    sample.setTaxon(mlTaxonObject.getCharacteristics().get(0).getValue().getAnnotationValue());
                }
            }
        }

        //set factor values and source information into attributes
        Map<String, Collection<Attribute>> usiSampleFactor = convertToUSISampleAttributes(source.getFactorValues());
        Map<String, Collection<Attribute>> usiSampleSource = convertToUSIAttributes(source.getDerivesFrom());
        usiSampleFactor.putAll(usiSampleSource);
        sample.setAttributes(usiSampleFactor);
        return sample;
    }

    private Source extractTaxonObject(uk.ac.ebi.subs.metabolights.model.Sample sample) {
        if(sample.getDerivesFrom() == null){
               return null;
        }
        if (sample.getDerivesFrom().size() > 0) {
            List<Source> derivesFrom = sample.getDerivesFrom();
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
            if(factorValue.getCategory()!=null && factorValue.getValue()!=null){
                //            String description; todo description is ignored for samples
                String key = factorValue.getCategory().getFactorName();

                Attribute attribute = new Attribute();
                if(factorValue.getValue() !=null){
                   if(LinkedHashMap.class == factorValue.getValue().getClass()){
                       LinkedHashMap value = (LinkedHashMap) factorValue.getValue();
                       if(value.containsKey("annotationValue")){
                          String annotationValue = (String) value.get("annotationValue");
                          attribute.setValue(annotationValue);
                       }
                       if(value.containsKey("termAccession")){
                           String termAccession = (String) value.get("termAccession");
                           if(termAccession!= null && !termAccession.isEmpty()){
                               Term term = new Term();
                               term.setUrl(termAccession);
                               attribute.setTerms(Arrays.asList(term));
                           }
                       }
                   }  else if (String.class == factorValue.getValue().getClass()){
                       attribute.setValue((String) factorValue.getValue());
                   }
                   else if (Integer.class == factorValue.getValue().getClass()){
                       attribute.setValue(((Integer)factorValue.getValue()).toString());
                   }
                }

                if (factorValue.getUnit() != null) {
                    attribute.setUnits(factorValue.getUnit().getAnnotationValue());
                }

               usiSampleAttributes.put(key, Arrays.asList(attribute));
            }
        }
        return usiSampleAttributes;
    }

    private String extractSampleFactorValue(Object value){
        return "";

    }


    private Map<String, Collection<Attribute>> convertToUSIAttributes(List<Source> derivesFrom) {
        Map<String, Collection<Attribute>> usiSampleAttributes = new HashMap<>();
        for (Source source : derivesFrom) {
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
