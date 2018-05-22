package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.MLFile;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;
import uk.ac.ebi.subs.metabolights.model.Study;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by kalai on 18/12/2017.
 */
public class USISubmissionToMLStudy implements Converter<uk.ac.ebi.subs.processing.SubmissionEnvelope, Study> {

    USIContactsToMLContacts usiContactsToMLContacts = new USIContactsToMLContacts();
    USIPublicationToMLPublication usiPublicationToMLPublication = new USIPublicationToMLPublication();
    USISampleToMLSample usiSampleToMLSample = new USISampleToMLSample();
    USIProtocolToMLProtocol usiProtocolToMLProtocol = new USIProtocolToMLProtocol();
    USIFactorToMLFactor usiFactorToMLFactor = new USIFactorToMLFactor();
    USIDescriptorToMLDescriptor usiDescriptorToMLDescriptor = new USIDescriptorToMLDescriptor();
    USIAssayToMLAssay usiAssayToMLAssay = new USIAssayToMLAssay();
    USIFileToMLFile usiFileToMLFile = new USIFileToMLFile();

    @Override
    public Study convert(uk.ac.ebi.subs.processing.SubmissionEnvelope source) {
        //todo handle multiple studies
        Study mlStudy = new Study();
        mlStudy.setIdentifier(source.getStudies().get(0).getAccession());
        mlStudy.setTitle(source.getStudies().get(0).getTitle());
        mlStudy.setDescription(source.getStudies().get(0).getDescription());                                                    


        mlStudy.setSubmissionDate(source.getSubmission().getSubmissionDate().toString());
        mlStudy.setPublicReleaseDate(source.getProjects().get(0).getReleaseDate().toString());
        mlStudy.setPublications(convertPublications(source.getProjects().get(0).getPublications()));
        mlStudy.setPeople(convertContacts(source.getProjects().get(0).getContacts()));

        mlStudy.setSamples(convertSamples(source.getSamples()));
        mlStudy.setAssays(convertAssays(source.getAssays()));
        mlStudy.setProtocols(convertProtocols(source.getProtocols()));
        mlStudy.setFactors(convertFactors(source.getStudies().get(0).getAttributes()));
        mlStudy.setStudyDesignDescriptors(convertDescriptors(source.getStudies().get(0).getAttributes()));

        assignDataFiles(mlStudy.getAssays(), source.getAssayData());

        return mlStudy;
    }


    private List<OntologyModel> convertDescriptors(Map<String, Collection<Attribute>> attributes) {
        List<OntologyModel> studyDesignDescriptors = new ArrayList<>();
        for (Map.Entry<String, Collection<Attribute>> entry : attributes.entrySet()) {
            if (entry.getKey().equals("studyDesignDescriptors")) {
                for (Attribute attribute : entry.getValue()) {
                    studyDesignDescriptors.add(usiDescriptorToMLDescriptor.convert(attribute));
                }
                return studyDesignDescriptors;
            }
        }
        return studyDesignDescriptors;
    }

    private List<Factor> convertFactors(Map<String, Collection<Attribute>> attributes) {
        List<Factor> factors = new ArrayList<>();
        for (Map.Entry<String, Collection<Attribute>> entry : attributes.entrySet()) {
            if (entry.getKey().equals("factors")) {
                for (Attribute attribute : entry.getValue()) {
                    factors.add(usiFactorToMLFactor.convert(attribute));
                }
                return factors;
            }
        }
        return factors;
    }

    private List<uk.ac.ebi.subs.metabolights.model.Protocol> convertProtocols(List<Protocol> protocols) {
        List<uk.ac.ebi.subs.metabolights.model.Protocol> mlProtocols = new ArrayList<>();
        for (Protocol protocol : protocols) {
            mlProtocols.add(usiProtocolToMLProtocol.convert(protocol));
        }
        return mlProtocols;
    }

    private List<uk.ac.ebi.subs.metabolights.model.Assay> convertAssays(List<Assay> assays) {
        List<uk.ac.ebi.subs.metabolights.model.Assay> mlAssays = new ArrayList<>();
        for (Assay assay : assays) {
            mlAssays.add(usiAssayToMLAssay.convert(assay));
        }
        return mlAssays;
    }

    private List<uk.ac.ebi.subs.metabolights.model.Sample> convertSamples(List<Sample> samples) {
        List<uk.ac.ebi.subs.metabolights.model.Sample> mlSamples = new ArrayList<>();
        for (Sample sample : samples) {
            mlSamples.add(usiSampleToMLSample.convert(sample));
        }
        return mlSamples;
    }

    private List<uk.ac.ebi.subs.metabolights.model.Contact> convertContacts(List<Contact> contacts) {
        List<uk.ac.ebi.subs.metabolights.model.Contact> people = new ArrayList<>();
        for (Contact contact : contacts) {
            people.add(usiContactsToMLContacts.convert(contact));
        }
        return people;
    }

    private List<uk.ac.ebi.subs.metabolights.model.Publication> convertPublications(List<Publication> publications) {
        List<uk.ac.ebi.subs.metabolights.model.Publication> mlPublications = new ArrayList<>();
        for (Publication publication : publications) {
            mlPublications.add(usiPublicationToMLPublication.convert(publication));
        }
        return mlPublications;
    }

    private void assignDataFiles(List<uk.ac.ebi.subs.metabolights.model.Assay> assays, List<AssayData> assayDataList) {
        for(AssayData assayData : assayDataList){
             for(File file : assayData.getFiles()){
                  if(file != null){
                      MLFile mlFile = usiFileToMLFile.convert(file);
                      for(AssayRef assayRef : assayData.getAssayRefs()){
                          for(uk.ac.ebi.subs.metabolights.model.Assay assay : assays){
                              if(assayRef.getAlias().equals(assay.getFilename())){
                                  assay.getDataFiles().add(mlFile);
                              }
                          }
                      }
                  }

             }
        }
    }


}
