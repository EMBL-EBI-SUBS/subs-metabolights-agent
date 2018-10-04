package uk.ac.ebi.subs.metabolights.converters;

import lombok.Data;
import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.*;
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
import java.util.List;

@Data
public class MLProjectToUSISubmission implements Converter<uk.ac.ebi.subs.metabolights.model.Project, uk.ac.ebi.subs.processing.SubmissionEnvelope> {

    MLSampleToUSISample mlSampleToUSISample = new MLSampleToUSISample();
    MLProtocolToUSIProtocol protocolToUSIProtocol = new MLProtocolToUSIProtocol();
    MLAssayToUSIAssay mlAssayToUSIAssay = new MLAssayToUSIAssay();
    MLFileToUSIFile mlFileToUSIFile = new MLFileToUSIFile();
    MLStudyToUSIStudy mlStudyToUSIStudy = new MLStudyToUSIStudy();
    MLProjectToUSIProject mlProjectToUSIProject = new MLProjectToUSIProject();

    @Override
    public uk.ac.ebi.subs.processing.SubmissionEnvelope convert(uk.ac.ebi.subs.metabolights.model.Project source) {


        Project usiProject = mlProjectToUSIProject.convert(source);

        //todo Store team in ML model
        Team team = new Team();
        team.setName("ML_test_team");
        usiProject.setTeam(team);

        List<Study> usiStudies = new ArrayList<>();
        List<Protocol> usiProtocols = new ArrayList<>();
        List<Sample> usiSamples = new ArrayList<>();
        List<Assay> usiAssays = new ArrayList<>();
        List<AssayData> usiAssayDataList = new ArrayList<>();
        List<Analysis> usiAnalysis = new ArrayList<>();


        for (uk.ac.ebi.subs.metabolights.model.Study mlStudy : source.getStudies()) {
            List<Protocol> protocols = convertProtocols(mlStudy.getProtocols(), team);
            List<Sample> samples = convertSamples(mlStudy.getSamples(), team);

            Study usiStudy = mlStudyToUSIStudy.convert(mlStudy);
            addStudyMetadata(usiStudy, team, usiProject, protocols);
            usiStudies.add(usiStudy);

            List<Assay> assays = convertAssays(mlStudy.getAssays(), team, usiStudy, usiAssayDataList);
            setProtocolUse(assays, protocols);
            setSampleUse(assays, samples);

            Analysis analysis = new Analysis();
            analysis.setTeam(team);
            analysis.getStudyRefs().add((StudyRef) usiStudy.asRef());
            analysis.setProtocolUses(getProtocolUse(protocols));
            analysis.setAssayRefs(getAssayReferences(assays));
            analysis.setAssayDataRefs(getAssayDataReferences(usiAssayDataList));
            analysis.setSampleRefs(getSampleReferences(samples));

            usiProtocols.addAll(protocols);
            usiSamples.addAll(samples);
            usiAssays.addAll(assays);
            usiAnalysis.add(analysis);
        }


        Submission submission = new Submission();
        if (source.getSubmissionDate() != null && !source.getSubmissionDate().isEmpty()) {
            submission.setSubmissionDate(java.sql.Date.valueOf(LocalDate.parse(source.getSubmissionDate())));
        }
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope(submission);
        submissionEnvelope.getSubmission().setTeam(team);
        submissionEnvelope.getProjects().add(usiProject);
        submissionEnvelope.setStudies(usiStudies);
        submissionEnvelope.setProtocols(usiProtocols);
        submissionEnvelope.setSamples(usiSamples);
        submissionEnvelope.setAssays(usiAssays);
        submissionEnvelope.setAssayData(usiAssayDataList);
        submissionEnvelope.setAnalyses(usiAnalysis);


        return submissionEnvelope;
    }


    private uk.ac.ebi.subs.data.submittable.Study addStudyMetadata(Study usiStudy, Team team, Project project, List<Protocol> protocols) {
        usiStudy.setTeam(team);
        usiStudy.setProjectRef((ProjectRef) project.asRef());
        List<ProtocolRef> protocolRefs = new ArrayList<>();
        for (Protocol protocol : protocols) {
            protocolRefs.add((ProtocolRef) protocol.asRef());
        }
        usiStudy.setProtocolRefs(protocolRefs);
        return usiStudy;
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
            usiAssays.add(assay);
        }
        return usiAssays;
    }

    private List<AssayData> convertDataFiles(List<MLFile> files, AssayRef assayRef, Team team) {
        List<AssayData> assayDataList = new ArrayList<>();
        List<AssayRef> assayRefs = Arrays.asList(assayRef);

        if (files != null && files.size() > 0) {
            for (MLFile mlfile : files) {
                AssayData assayData = new AssayData();
                assayData.setAlias(mlfile.getFilename());
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

    private List<ProtocolUse> getProtocolUse(List<Protocol> usiProtocols) {
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
        for (Assay assay : usiAssays) {
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
