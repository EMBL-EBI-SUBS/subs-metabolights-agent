package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.metabolights.model.*;
import uk.ac.ebi.subs.metabolights.model.MLFile;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by kalai on 09/02/2018.
 */
public class StudyToSubmissionTest {

    @Test
    public void generateJson() {
        MLStudyToUSISubmission mlStudyToUSISubmission = new MLStudyToUSISubmission();
        SubmissionEnvelope submissionEnvelope = null;
        try {
//            submissionEnvelope = createUSIStudySubmission();
            uk.ac.ebi.subs.metabolights.model.Study mlStudy = WSUtils.getMLStudyFromDisc();
            submissionEnvelope = mlStudyToUSISubmission.convert(mlStudy);
            ObjectMapper mapper = new ObjectMapper();
            String submissionJSON = mapper.writeValueAsString(submissionEnvelope);
            System.out.println(submissionJSON);
            String submissionSample = mapper.writeValueAsString(submissionEnvelope.getSamples());
            //System.out.println(submissionSample);

            assertEquals(submissionEnvelope.getSamples().size(), 16);
            assertEquals(submissionEnvelope.getProtocols().size(), 6);
            assertEquals(submissionEnvelope.getSubmission().getTeam().getName(), "ML_test_team");
            assertEquals(submissionEnvelope.getProjects().iterator().next().getContacts().size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SubmissionEnvelope createUSIStudySubmission() throws JsonProcessingException {

       // uk.ac.ebi.subs.metabolights.model.Study mlStudy = WSUtils.getMLStudy("MTBLS2");
        uk.ac.ebi.subs.metabolights.model.Study mlStudy = WSUtils.getMLStudyFromDisc();
        MLContactsToUSIContacts toUsiContacts = new MLContactsToUSIContacts();
        MLPublicationToUSIPublication toUSIPublication = new MLPublicationToUSIPublication();
        MLSampleToUSISample mlSampleToUSISample = new MLSampleToUSISample();
        MLProtocolToUSIProtocol protocolToUSIProtocol = new MLProtocolToUSIProtocol();
        MLFactorToUSIFactor mlFactorToUSIFactor = new MLFactorToUSIFactor();
        MLDescriptorToUSIDescriptor mlDescriptorToUSIDescriptor = new MLDescriptorToUSIDescriptor();


        Team team = new Team();
        team.setName("ML_test_team");

        //project
        Project usiProject = new Project();
        usiProject.setTeam(team);
        usiProject.setReleaseDate(LocalDate.parse(mlStudy.getPublicReleaseDate()));

        //contacts
        List<Contact> usiContacts = new ArrayList<>();
        for (uk.ac.ebi.subs.metabolights.model.Contact studyContact : mlStudy.getPeople()) {
            uk.ac.ebi.subs.data.component.Contact usiContact = toUsiContacts.convert(studyContact);
            usiContacts.add(usiContact);
        }
        usiProject.setContacts(usiContacts);

        //publications
        List<Publication> usiPublications = new ArrayList<>();
        for (uk.ac.ebi.subs.metabolights.model.Publication publication : mlStudy.getPublications()) {
            Publication usiPublication = toUSIPublication.convert(publication);
            usiPublications.add(usiPublication);
        }
        usiProject.setPublications(usiPublications);

        // samples
        List<uk.ac.ebi.subs.metabolights.model.Sample> mlStudySamples = WSUtils.getMLStudySamples("MTBLS2");
        List<Sample> usiSamples = new ArrayList<>();
        for (uk.ac.ebi.subs.metabolights.model.Sample mlSample : mlStudySamples) {
            Sample usiSample = mlSampleToUSISample.convert(mlSample);
            usiSample.setTeam(team);
            usiSamples.add(usiSample);
        }

        // study
        Study usiStudy = new Study();
        usiStudy.setStudyType(StudyDataType.Metabolomics);
        usiStudy.setAlias("MTBLS2_TEST");
        usiStudy.setTeam(team);
        usiStudy.setTitle(mlStudy.getTitle());
        usiStudy.setAccession(mlStudy.getIdentifier());
        usiStudy.setDescription(mlStudy.getDescription());
        usiStudy.setProjectRef((ProjectRef) usiProject.asRef());


        //protocols

        List<uk.ac.ebi.subs.metabolights.model.Protocol> mlStudyProtocols = WSUtils.getMLStudyProtocols("MTBLS2");
        List<Protocol> usiProtocols = new ArrayList<>();
        for (uk.ac.ebi.subs.metabolights.model.Protocol studyProtocol : mlStudyProtocols) {
            Protocol usiProtocol = protocolToUSIProtocol.convert(studyProtocol);
            usiProtocol.setTeam(team);
            usiProtocols.add(usiProtocol);
        }

        //study descriptors
        List<Attribute> studyDescriptorAttributes = new ArrayList<>();
        if (mlStudy.getStudyDesignDescriptors() != null && mlStudy.getStudyDesignDescriptors().size() > 0) {
            for(OntologyModel descriptor : mlStudy.getStudyDesignDescriptors()){
                studyDescriptorAttributes.add(mlDescriptorToUSIDescriptor.convert(descriptor));
            }
        }
        usiStudy.getAttributes().put("studyDesignDescriptors", studyDescriptorAttributes);

        //factors
        List<Attribute> studyFactorAttributes = new ArrayList<>();
        if (mlStudy.getFactors() != null && mlStudy.getFactors().size() > 0) {
           for(Factor factor : mlStudy.getFactors()){
                studyFactorAttributes.add(mlFactorToUSIFactor.convert(factor));
            }
        }
        usiStudy.getAttributes().put("factors", studyFactorAttributes);


//        as = new Assay();
//        as.setArchive(Archive.ArrayExpress);
//        as.setAlias("exp1");
//        as.getSampleUses().add(new SampleUse((SampleRef) sa.asRef()));
//        as.setStudyRef((StudyRef)st.asRef());
//        as.setTeam(team);

        //todo handle multiple assays

        MLAssayToUSIAssay toUSIAssay = new MLAssayToUSIAssay();
        Assay assay = toUSIAssay.convert(mlStudy.getAssays().get(0));
        assay.setStudyRef((StudyRef) usiStudy.asRef());
        assay.setTeam(team);

        // protocol and sample use
        for (Protocol usiProtocol : usiProtocols) {
            ProtocolUse protocolUse = new ProtocolUse();
            protocolUse.setProtocolRef((ProtocolRef) usiProtocol.asRef());
            assay.getProtocolUses().add(protocolUse);
        }
        for (Sample usiSample : usiSamples) {
            SampleUse sampleUse = new SampleUse();
            sampleUse.setSampleRef((SampleRef) usiSample.asRef());
            assay.getSampleUses().add(sampleUse);
        }


        //todo figure out assay data
        List<AssayData> assayDataList = new ArrayList<>();
        List<AssayRef> assayRefs = Arrays.asList((AssayRef) assay.asRef());
        if (mlStudy.getAssays().size() > 0) {
            uk.ac.ebi.subs.metabolights.model.Assay metaboliteAssay = mlStudy.getAssays().get(0);
            if (metaboliteAssay.getDataFiles() != null && metaboliteAssay.getDataFiles().size() > 0) {
                for (MLFile mlfile : metaboliteAssay.getDataFiles()) {
                    AssayData assayData = new AssayData();
                    assayData.setTeam(team);
                    assayData.setAssayRefs(assayRefs);
                    if (mlfile != null) {
                        uk.ac.ebi.subs.data.component.File usiFile = new uk.ac.ebi.subs.data.component.File();
                        usiFile.setType(mlfile.getLabel());
                        usiFile.setName(mlfile.getFilename());
                        assayData.setFiles(Arrays.asList(usiFile));
                        assayDataList.add(assayData);
                    }
                }
            }
        }


        //Analysis

        Analysis usiAnalysis = new Analysis();
        usiAnalysis.setTeam(team);
        usiAnalysis.getStudyRefs().add((StudyRef) usiStudy.asRef());
        usiAnalysis.getAssayRefs().add((AssayRef) assay.asRef());
        for (AssayData assayData : assayDataList) {
            usiAnalysis.getAssayDataRefs().add((AssayDataRef) assayData.asRef());
        }
        for (Protocol usiProtocol : usiProtocols) {
            ProtocolUse protocolUse = new ProtocolUse();
            protocolUse.setProtocolRef((ProtocolRef) usiProtocol.asRef());
            usiAnalysis.getProtocolUses().add(protocolUse);
        }
        for (Sample usiSample : usiSamples) {
            usiAnalysis.getSampleRefs().add((SampleRef) usiSample.asRef());
        }


        Submission submission = new Submission();
        submission.setSubmissionDate(java.sql.Date.valueOf(LocalDate.parse(mlStudy.getSubmissionDate())));
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope(submission);
        submissionEnvelope.getSubmission().setTeam(team);
        submissionEnvelope.getProjects().add(usiProject);
        submissionEnvelope.getStudies().add(usiStudy);
        submissionEnvelope.setProtocols(usiProtocols);
        submissionEnvelope.setSamples(usiSamples);
        submissionEnvelope.getAssays().add(assay);
        submissionEnvelope.setAssayData(assayDataList);
        submissionEnvelope.getAnalyses().add(usiAnalysis);

        //todo from isa_json ontologySourceReferences are not included. Unsure of its use.

        return submissionEnvelope;

    }


}
