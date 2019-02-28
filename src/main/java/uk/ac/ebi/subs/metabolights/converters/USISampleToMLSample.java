package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Term;
import uk.ac.ebi.subs.metabolights.model.*;

import java.util.*;

/**
 * Created by kalai on 11/01/2018.
 */
@Service
public class USISampleToMLSample implements Converter<uk.ac.ebi.subs.data.submittable.Sample, Sample> {

    private String ncbiBaseUrl = "http://purl.obolibrary.org/obo/NCBITaxon_";

    @Override
    public Sample convert(uk.ac.ebi.subs.data.submittable.Sample source) {
        Sample sample = new Sample();
        //todo set accession
        //todo set team
        // todo set description - into comments?
        // todo set release date

        //set name
        sample.setName(source.getAlias());

        // set taxon and title
//        Source mlTaxonObject = getTaxonObject();
//        mlTaxonObject.setName(source.getTitle());
//        if(source.getTaxonId() != null){
//            mlTaxonObject.getCharacteristics().get(0).getValue().setTermAccession(ncbiBaseUrl + source.getTaxonId());
//        }

        //set attributes (Factor values)
        sample.setFactorValues(convertToMLSampleAttributes(source.getAttributes()));
        // set derives from source information
        sample.setDerivesFrom(convertToMLSampleSource(source.getAttributes(), source.getTitle()));
        sample.setComments(ConverterUtils.convertIndexInfoToComments(source.getAttributes()));
        return sample;
    }


    private Source getTaxonObject() {
        Source sampleSource = new Source();
        sampleSource.setCharacteristics(Arrays.asList(new SampleSourceOntologyModel()));
        return sampleSource;
    }

    private List<SampleFactorValue> convertToMLSampleAttributes(Map<String, Collection<Attribute>> usiAttributes) {
        List<SampleFactorValue> mlSampleFactorValues = new ArrayList<SampleFactorValue>();

        for (Map.Entry<String, Collection<Attribute>> entry : usiAttributes.entrySet()) {
            if (!entry.getKey().toLowerCase().equals("organism") && !entry.getKey().toLowerCase().equals("organism part") && !entry.getKey().toLowerCase().equals("variant")) {
                if (entry.getValue().size() > 0) {
                    Attribute attribute = entry.getValue().iterator().next();
                    SampleFactorValue sampleFactorValue = new SampleFactorValue();
                    OntologyModel model = new OntologyModel();
                    model.setAnnotationValue(attribute.getValue());

                    sampleFactorValue.getCategory().setFactorName(entry.getKey());
                    sampleFactorValue.getCategory().getFactorType().setAnnotationValue(entry.getKey());


                    sampleFactorValue.setValue(model);
                    String url = "";
                    if (attribute.getTerms().size() > 0) {
                        url = attribute.getTerms().iterator().next().getUrl();
                        ((OntologyModel) sampleFactorValue.getValue()).setTermAccession(url);
                    }
                    sampleFactorValue.getUnit().setAnnotationValue(attribute.getUnits() == null ? "" : attribute.getUnits());
                    // by the way USI stores attributes, unit url might be present in the attribute terms list but is tricky to pick the matching one and assign.
                    // hence assigning urls to unit is ignored for now. Only value is assigned to unit.
                    mlSampleFactorValues.add(sampleFactorValue);
                }
            }

        }
        return mlSampleFactorValues;
    }


    private List<Source> convertToMLSampleSource(Map<String, Collection<Attribute>> usiAttributes, String title) {
        List<Source> sampleSource = new ArrayList<>();
        Source source = new Source();
        source.setName(title);
        //todo check for multiple derivesFrom entries. Only one entry in the List is assumed
        for (Map.Entry<String, Collection<Attribute>> entry : usiAttributes.entrySet()) {
            if (entry.getKey().toLowerCase().equals("organism") || entry.getKey().toLowerCase().equals("organism part") || entry.getKey().toLowerCase().equals("variant")) {
                if (entry.getValue().size() > 0) {
                    Attribute attribute = entry.getValue().iterator().next();

                    SampleSourceOntologyModel sampleSourceOntologyModel = new SampleSourceOntologyModel();

                    sampleSourceOntologyModel.getCategory().setAnnotationValue(entry.getKey());
                    String url = "";
                    if (attribute.getTerms().size() > 0) {
                        url = attribute.getTerms().iterator().next().getUrl();
                        sampleSourceOntologyModel.getValue().setTermAccession(url);
                    }
                    sampleSourceOntologyModel.getValue().
                            setAnnotationValue(
                                    attribute.getValue() == null ? "" : attribute.getValue());
                    sampleSourceOntologyModel.getUnit().
                            setAnnotationValue(
                                    attribute.getUnits() == null ? "" : attribute.getUnits());

                    source.getCharacteristics().add(sampleSourceOntologyModel);
                }
            }

        }
        sampleSource.add(source);
        return sampleSource;
    }
}
