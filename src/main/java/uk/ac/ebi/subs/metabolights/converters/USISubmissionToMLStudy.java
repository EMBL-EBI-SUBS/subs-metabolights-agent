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
public class USISubmissionToMLStudy implements Converter<uk.ac.ebi.subs.processing.SubmissionEnvelope, List<Study>> {

    USIContactsToMLContacts usiContactsToMLContacts = new USIContactsToMLContacts();
    USIPublicationToMLPublication usiPublicationToMLPublication = new USIPublicationToMLPublication();
    USISampleToMLSample usiSampleToMLSample = new USISampleToMLSample();
    USIProtocolToMLProtocol usiProtocolToMLProtocol = new USIProtocolToMLProtocol();
    USIFactorToMLFactor usiFactorToMLFactor = new USIFactorToMLFactor();
    USIDescriptorToMLDescriptor usiDescriptorToMLDescriptor = new USIDescriptorToMLDescriptor();
    USIAssayToMLAssay usiAssayToMLAssay = new USIAssayToMLAssay();
    USIFileToMLFile usiFileToMLFile = new USIFileToMLFile();
    USIStudyToMLStudy usiStudyToMLStudy = new USIStudyToMLStudy();

    @Override
    public List<Study> convert(uk.ac.ebi.subs.processing.SubmissionEnvelope source) {
        List<Study> mlStudies = new ArrayList<Study>();
        for(uk.ac.ebi.subs.data.submittable.Study usiStudy : source.getStudies()){
            Study mlStudy = usiStudyToMLStudy.convert(usiStudy);
            //todo handle multiple project scenarios
            //todo assign objects based on accession reference
            mlStudy.setSubmissionDate(source.getSubmission().getSubmissionDate().toString());
            mlStudy.setPublicReleaseDate(source.getProjects().get(0).getReleaseDate().toString());
            mlStudy.setPublications(convertPublications(source.getProjects().get(0).getPublications()));
            mlStudy.setPeople(convertContacts(source.getProjects().get(0).getContacts()));

            List<uk.ac.ebi.subs.metabolights.model.Sample> samples = convertSamples(source.getSamples());
            mlStudy.setSamples(samples);
            mlStudy.setAssays(convertAssays(source.getAssays()));
            mlStudy.setProtocols(convertProtocols(source.getProtocols()));

            //assignDataFiles(mlStudy.getAssays(), source.getAssayData());
            assignDataFiles(mlStudy.getAssays(), source.getAssayData(), samples);
            mlStudies.add(mlStudy);

        }
        return mlStudies;
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

//    private void assignDataFiles(List<uk.ac.ebi.subs.metabolights.model.Assay> assays, List<AssayData> assayDataList, List<uk.ac.ebi.subs.metabolights.model.Sample> samples) {
//        for(AssayData assayData : assayDataList){
//             for(File file : assayData.getFiles()){
//                  if(file != null){
//                      MLFile mlFile = usiFileToMLFile.convert(file);
//                      for(AssayRef assayRef : assayData.getAssayRefs()){
//                          for(uk.ac.ebi.subs.metabolights.model.Assay assay : assays){
//                              if(assayRef.getAlias().equals(assay.getFilename())){
//                                  assay.getDataFiles().add(mlFile);
//                              }
//                          }
//                      }
//                  }
//
//             }
//        }
//    }

    private void assignDataFiles(List<uk.ac.ebi.subs.metabolights.model.Assay> assays, List<AssayData> assayDataList, List<uk.ac.ebi.subs.metabolights.model.Sample> samples) {
        for(AssayData assayData : assayDataList){
            for(File file : assayData.getFiles()){
                if(file != null){
                    MLFile mlFile = usiFileToMLFile.convert(file);

                    if(file.getType().toLowerCase().equalsIgnoreCase("Metabolite Assignment File")){
                       mlFile.setGeneratedFrom(samples);
                    }

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
