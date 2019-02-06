package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.metabolights.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by kalai on 18/12/2017.
 */
public class USISubmissionToMLProject implements Converter<uk.ac.ebi.subs.processing.SubmissionEnvelope, Project> {

    USISampleToMLSample usiSampleToMLSample = new USISampleToMLSample();
    USIProtocolToMLProtocol usiProtocolToMLProtocol = new USIProtocolToMLProtocol();
    USIAssayToMLAssay usiAssayToMLAssay = new USIAssayToMLAssay();
    USIFileToMLFile usiFileToMLFile = new USIFileToMLFile();
    USIStudyToMLStudy usiStudyToMLStudy = new USIStudyToMLStudy();
    USIProjectToMLProject usiProjectToMLProject = new USIProjectToMLProject();

    @Override
    public Project convert(uk.ac.ebi.subs.processing.SubmissionEnvelope source) {
        //todo handle multiple project scenarios
        //todo assign objects based on accession reference
        Project mlProject = usiProjectToMLProject.convert(source.getProjects().get(0));
        List<Study> mlStudies = new ArrayList<Study>();
        for (uk.ac.ebi.subs.data.submittable.Study usiStudy : source.getStudies()) {
            Study mlStudy = usiStudyToMLStudy.convert(usiStudy);
            if (source.getSubmission().getSubmissionDate() != null) {
                mlStudy.setSubmissionDate(source.getSubmission().getSubmissionDate().toString());
                mlProject.setSubmissionDate(source.getSubmission().getSubmissionDate().toString());
            }
            if (source.getProjects().get(0).getReleaseDate() != null) {
                mlStudy.setPublicReleaseDate(source.getProjects().get(0).getReleaseDate().toString());
            }
            List<uk.ac.ebi.subs.metabolights.model.Sample> samples = convertSamples(source.getSamples());
            mlStudy.setSamples(samples);
            mlStudy.setAssays(convertAssays(source.getAssays()));
            mlStudy.setProtocols(convertProtocols(source.getProtocols()));

            assignDataFiles(mlStudy.getAssays(), source.getAssayData(), samples);
            mlStudies.add(mlStudy);

        }
        mlProject.setStudies(mlStudies);
        return mlProject;
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

    private void assignDataFiles(List<uk.ac.ebi.subs.metabolights.model.Assay> assays, List<AssayData> assayDataList, List<uk.ac.ebi.subs.metabolights.model.Sample> samples) {
        for (AssayData assayData : assayDataList) {
            for (File file : assayData.getFiles()) {
                if (file != null) {
                    MLFile mlFile = usiFileToMLFile.convert(file);
                    /*
                     The traditional ISA assay model links all the sample files to MAF files under "generatedFrom". Hence samples are added for MAF alone.
                     */

                    if (file.getType().toLowerCase().equalsIgnoreCase("Metabolite Assignment File")) {
                        mlFile.setGeneratedFrom(samples);
                    }

                    for (AssayRef assayRef : assayData.getAssayRefs()) {
                        for (uk.ac.ebi.subs.metabolights.model.Assay assay : assays) {
                            if (assayRef.getAlias().equals(assay.getFilename())) {
                                assay.getDataFiles().add(mlFile);
                            }
                        }
                    }
                }

            }
        }
    }

}
