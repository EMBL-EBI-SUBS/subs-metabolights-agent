package uk.ac.ebi.subs.metabolights.converters;

import lombok.Data;
import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.metabolights.model.*;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class MLStudyToUSISubmission implements Converter<uk.ac.ebi.subs.metabolights.model.Study, uk.ac.ebi.subs.processing.SubmissionEnvelope> {

    //todo consider changing to ML Project -> submission. OntologySourceReferences are currently excluded
    MLContactsToUSIContacts toUsiContacts = new MLContactsToUSIContacts();
    MLPublicationToUSIPublication toUSIPublication = new MLPublicationToUSIPublication();
    MLSampleToUSISample mlSampleToUSISample = new MLSampleToUSISample();
    MLProtocolToUSIProtocol protocolToUSIProtocol = new MLProtocolToUSIProtocol();
    MLAssayToUSIAssay mlAssayToUSIAssay = new MLAssayToUSIAssay();
    MLFileToUSIFile mlFileToUSIFile = new MLFileToUSIFile();
    MLStudyToUSIStudy mlStudyToUSIStudy = new MLStudyToUSIStudy();

    @Override
    public uk.ac.ebi.subs.processing.SubmissionEnvelope convert(uk.ac.ebi.subs.metabolights.model.Study source) {


        Project project = new Project();
        //todo Store team in ML model
        Team team = new Team();
        team.setName("ML_test_team");
        project.setTeam(team);
        project.setReleaseDate(LocalDate.parse(source.getPublicReleaseDate()));


        project.setContacts(convertContacts(source.getPeople()));
        project.setPublications(convertPublications(source.getPublications()));

        Study usiStudy =  mlStudyToUSIStudy.convert(source);
        addStudyMetadata(usiStudy, team, project);

        List<Protocol> protocols = convertProtocols(source.getProtocols(), team);
        List<Sample> samples = convertSamples(source.getSamples(), team);

        List<AssayData> assayDataList = new ArrayList<>();
        List<Assay> usiAssays = convertAssays(source.getAssays(), team, usiStudy, assayDataList);
        setProtocolUse(usiAssays, protocols);
        setSampleUse(usiAssays, samples);

        Analysis usiAnalysis = new Analysis();
        usiAnalysis.setTeam(team);
        usiAnalysis.getStudyRefs().add((StudyRef) usiStudy.asRef());
        usiAnalysis.setProtocolUses(getProtocolUse(protocols));
        usiAnalysis.setAssayRefs(getAssayReferences(usiAssays));
        usiAnalysis.setAssayDataRefs(getAssayDataReferences(assayDataList));
        usiAnalysis.setSampleRefs(getSampleReferences(samples));

        Submission submission = new Submission();
        //submission.setSubmissionDate(java.sql.Date.valueOf(LocalDate.parse(source.getSubmissionDate())));
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope(submission);
        submissionEnvelope.getSubmission().setTeam(team);
        submissionEnvelope.getProjects().add(project);
        submissionEnvelope.getStudies().add(usiStudy);
        submissionEnvelope.setProtocols(protocols);
        submissionEnvelope.setSamples(samples);
        submissionEnvelope.setAssays(usiAssays);
        submissionEnvelope.setAssayData(assayDataList);
        submissionEnvelope.getAnalyses().add(usiAnalysis);


        return submissionEnvelope;
    }


    private uk.ac.ebi.subs.data.submittable.Study addStudyMetadata(uk.ac.ebi.subs.data.submittable.Study usiStudy, Team team, Project project) {
        usiStudy.setStudyType(StudyDataType.Metabolomics);
        usiStudy.setAlias("");
        usiStudy.setTeam(team);
        usiStudy.setProjectRef((ProjectRef) project.asRef());
        return usiStudy;
    }


    private List<Contact> convertContacts(List<uk.ac.ebi.subs.metabolights.model.Contact> studyContacts) {
        List<Contact> usiContacts = new ArrayList<>();
        for (uk.ac.ebi.subs.metabolights.model.Contact studyContact : studyContacts) {
            uk.ac.ebi.subs.data.component.Contact usiContact = toUsiContacts.convert(studyContact);
            usiContacts.add(usiContact);
        }
        return usiContacts;
    }


    private List<uk.ac.ebi.subs.data.component.Publication> convertPublications(List<uk.ac.ebi.subs.metabolights.model.Publication> publications) {
        List<uk.ac.ebi.subs.data.component.Publication> usiPublications = new ArrayList<>();
        for (uk.ac.ebi.subs.metabolights.model.Publication publication : publications) {
            uk.ac.ebi.subs.data.component.Publication usiPublication = toUSIPublication.convert(publication);
            usiPublications.add(usiPublication);
        }
        return usiPublications;
    }


    private List<Protocol> convertProtocols(List<uk.ac.ebi.subs.metabolights.model.Protocol> mlStudyProtocols, Team team) {
        List<Protocol> usiProtocols = new ArrayList<>();
        for (uk.ac.ebi.subs.metabolights.model.Protocol studyProtocol : mlStudyProtocols) {
            Protocol usiProtocol = protocolToUSIProtocol.convert(studyProtocol);
            usiProtocol.setTeam(team);
            usiProtocols.add(usiProtocol);
        }
        return usiProtocols;
    }


    private List<Sample> convertSamples(List<uk.ac.ebi.subs.metabolights.model.Sample> mlStudySamples, Team team) {
        List<Sample> usiSamples = new ArrayList<>();
        for (uk.ac.ebi.subs.metabolights.model.Sample mlSample : mlStudySamples) {
            Sample usiSample = mlSampleToUSISample.convert(mlSample);
            usiSample.setTeam(team);
            usiSamples.add(usiSample);
        }
        return usiSamples;
    }


    private List<Assay> convertAssays(List<uk.ac.ebi.subs.metabolights.model.Assay> assays, Team team, uk.ac.ebi.subs.data.submittable.Study usiStudy, List<AssayData> assayDataList) {
        List<Assay> usiAssays = new ArrayList<>();
        for (int i = 0; i < assays.size(); i++) {
            Assay assay = mlAssayToUSIAssay.convert(assays.get(i));
            assay.setStudyRef((StudyRef) usiStudy.asRef());
            assay.setTeam(team);
            assayDataList.addAll(convertDataFiles(assays.get(i).getDataFiles(), (AssayRef) assay.asRef(), team));
        }
        return usiAssays;
    }

    private List<AssayData> convertDataFiles(List<MLFile> files, AssayRef assayRef, Team team) {
        List<AssayData> assayDataList = new ArrayList<>();
        List<AssayRef> assayRefs = Arrays.asList(assayRef);

        if (files != null && files.size() > 0) {
            for (MLFile mlfile : files) {
                AssayData assayData = new AssayData();
                assayData.setTeam(team);
                assayData.setAssayRefs(assayRefs);
                if (mlfile != null) {
                    uk.ac.ebi.subs.data.component.File usiFile = mlFileToUSIFile.convert(mlfile);
                    assayData.setFiles(Arrays.asList(usiFile));
                    assayDataList.add(assayData);
                }
            }
        }
        return assayDataList;
    }


    private void setProtocolUse(List<Assay> usiAssays, List<Protocol> usiProtocols) {
        for (Assay assay : usiAssays) {
            assay.setProtocolUses(getProtocolUse(usiProtocols));
        }
    }

    private List<ProtocolUse> getProtocolUse(List<Protocol> usiProtocols){
        List<ProtocolUse> protocolUses = new ArrayList<>();
        for (Protocol usiProtocol : usiProtocols) {
            ProtocolUse protocolUse = new ProtocolUse();
            protocolUse.setProtocolRef((ProtocolRef) usiProtocol.asRef());
            protocolUses.add(protocolUse);
        }
        return protocolUses;
    }


    private void setSampleUse(List<Assay> usiAssays, List<Sample> samples) {
        for (Assay assay : usiAssays) {
            for (Sample usiSample : samples) {
                SampleUse sampleUse = new SampleUse();
                sampleUse.setSampleRef((SampleRef) usiSample.asRef());
                assay.getSampleUses().add(sampleUse);
            }
        }
    }


    private List<AssayRef> getAssayReferences(List<Assay> usiAssays) {
        List<AssayRef> assayRefs = new ArrayList<>();
        for(Assay assay : usiAssays){
            assayRefs.add((AssayRef) assay.asRef());
        }
        return assayRefs;
    }


    private List<SampleRef> getSampleReferences(List<Sample> samples) {
        List<SampleRef> sampleRefList = new ArrayList<>();
        for (Sample usiSample : samples) {
            sampleRefList.add((SampleRef) usiSample.asRef());
        }
        return sampleRefList;
    }

    private List<AssayDataRef> getAssayDataReferences(List<AssayData> assayDataList) {
        List<AssayDataRef> assayDataRefList = new ArrayList<>();
        for (AssayData assayData : assayDataList) {
            assayDataRefList.add((AssayDataRef) assayData.asRef());
        }
        return assayDataRefList;
    }



}
