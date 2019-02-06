package uk.ac.ebi.subs.metabolights.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;

import uk.ac.ebi.subs.metabolights.model.*;
import uk.ac.ebi.subs.metabolights.services.*;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MetaboLightsStudyProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MetaboLightsStudyProcessor.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    ProcessingCertificateGenerator certificatesGenerator;

    @Autowired
    FetchService fetchService;

    @Autowired
    PostService postService;

    @Autowired
    UpdateService updateService;

    @Autowired
    DeletionService deletionService;

    @Autowired
    public MetaboLightsStudyProcessor(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
    }

    protected List<ProcessingCertificate> processSamples(SubmissionEnvelope envelope) {
        Submission submission = envelope.getSubmission();

        logger.debug("Processing {} samples from {} submission", envelope.getSamples().size(), submission.getId());
        System.out.println("Processing {} samples from {} submission" + envelope.getSamples().size() + submission.getId());

        List<ProcessingCertificate> certificates = new ArrayList<>();

        List<Sample> samplesToUpdate = envelope.getSamples().stream()
                .filter(s -> (s.isAccessioned()))
                .collect(Collectors.toList());

        certificates.addAll(certificatesGenerator.generateCertificates(samplesToUpdate));

        // Submission
        List<Sample> samplesToSubmit = envelope.getSamples().stream()
                .filter(s -> !s.isAccessioned())
                .collect(Collectors.toList());
        //todo change incoming variables without using api calls and push back to queue

//        List<Sample> submittedSamples = submissionService.submit(samplesToSubmit);

        certificates.addAll(certificatesGenerator.generateCertificates(samplesToSubmit));

        return certificates;
    }

    ProcessingCertificateEnvelope processStudy(SubmissionEnvelope submissionEnvelope) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();
        for (Study study : submissionEnvelope.getStudies()) {
            //todo handle many studies in one project
            if (!study.isAccessioned()) {
                return createNewMetaboLightsStudy(study, submissionEnvelope);
            } else {
                processingCertificateList.addAll(processMetaData(study, submissionEnvelope, false));
            }
        }
        return new ProcessingCertificateEnvelope(submissionEnvelope.getSubmission().getId(), processingCertificateList);
    }

    ProcessingCertificateEnvelope createNewMetaboLightsStudy(Study study, SubmissionEnvelope submissionEnvelope) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();
        ProcessingCertificate processingCertificate = getNewCertificate();
        try {
            String accession = this.fetchService.createNewStudyAndGetAccession();
            study.setAccession(accession);
            processingCertificate.setAccession(accession);
            processingCertificate.setMessage("Study successfully accessioned");
        } catch (Exception e) {
            processingCertificate.setMessage("Error creating new study : " + e.getMessage());
            processingCertificateList.add(processingCertificate);
            return new ProcessingCertificateEnvelope(submissionEnvelope.getSubmission().getId(), processingCertificateList);
        }
        processingCertificateList.addAll(processMetaData(study, submissionEnvelope, true));
        processingCertificate.setProcessingStatus(ProcessingStatusEnum.Submitted);
        processingCertificateList.add(processingCertificate);
        return new ProcessingCertificateEnvelope(submissionEnvelope.getSubmission().getId(), processingCertificateList);
    }

    List<ProcessingCertificate> processMetaData(Study study, SubmissionEnvelope submissionEnvelope, boolean isNewSubmission) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();

        uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy = getStudyBy(study.getAccession());
        StudyFiles studyFiles = this.fetchService.getStudyFiles(study.getAccession());
        String sampleFileName = AgentProcessorUtils.getSampleFileName(studyFiles);
        List<String> assayFileNames = AgentProcessorUtils.getAssayFileName(studyFiles);

        // placeholders are removed in latest version
        // resetDummyValuesIn(existingMetaboLightsStudy, isNewSubmission, sampleFileName);

        if (existingMetaboLightsStudy == null) {
            ProcessingCertificate certificate = getNewCertificate();
            certificate.setAccession(study.getAccession());
            certificate.setMessage("Something went wrong while trying to access the existing metabolights study");
            processingCertificateList.add(certificate);
            return processingCertificateList;
        }

        update(processingCertificateList, processTitle(study));
        update(processingCertificateList, processDescription(study));
        update(processingCertificateList, processStudyFactors(study, existingMetaboLightsStudy));
        update(processingCertificateList, processStudyDescriptors(study, existingMetaboLightsStudy));
        update(processingCertificateList, processProtocols(study, submissionEnvelope.getProtocols(), existingMetaboLightsStudy));

        if (submissionEnvelope.getProjects() != null && submissionEnvelope.getProjects().size() > 0) {
//            //todo handle multiple projects
            update(processingCertificateList, processContacts(study, submissionEnvelope.getProjects().get(0), existingMetaboLightsStudy));
            update(processingCertificateList, processPublications(study, submissionEnvelope.getProjects().get(0), existingMetaboLightsStudy));
        }


        update(processingCertificateList, processSamples(study, submissionEnvelope.getSamples(), sampleFileName, isNewSubmission));

         /*
         from each assay we will get attributes, sample use, protocol use
         mostly for the purpose of using it as a cell in a row. the sample use. ref. alias should be suffice. If not the referenced ref must be extracted out of protocols and samples.

         for the purpose of updating metabolights


         if it is a new submission

         1. using assay attributes, the base assay structure has to be created. This will also create an a_file. File name can be obtained from the response.

         But here each assay is a row. ????


         */

        update(processingCertificateList, processAssays(study, submissionEnvelope.getAssays(), isNewSubmission, assayFileNames));

        return processingCertificateList;
    }

    private uk.ac.ebi.subs.metabolights.model.Study getStudyBy(String accession) {
        try {
            uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy = this.fetchService.getStudy(accession);
            return existingMetaboLightsStudy;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    private uk.ac.ebi.subs.metabolights.model.Study resetDummyValuesIn(uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy, boolean isNewSubmission, String sampleFileName) {
        if (isNewSubmission) {
            resetContacts(existingMetaboLightsStudy);
            resetPublications(existingMetaboLightsStudy);
            deleteDefaultRow(existingMetaboLightsStudy.getIdentifier(), sampleFileName);
            //todo delete default rows in assay sheet and MAF sheet 
            try {
                return this.fetchService.getStudy(existingMetaboLightsStudy.getIdentifier());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return existingMetaboLightsStudy;
    }

    private void resetContacts(uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy) {
        if (existingMetaboLightsStudy.getPeople() != null && existingMetaboLightsStudy.getPeople().size() == 1) {
            try {
                this.deletionService.deleteContact(existingMetaboLightsStudy.getIdentifier(), existingMetaboLightsStudy.getPeople().get(0).getEmail());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void resetPublications(uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy) {
        if (existingMetaboLightsStudy.getPublications() != null && existingMetaboLightsStudy.getPublications().size() == 1) {
            try {
                this.deletionService.deletePublication(existingMetaboLightsStudy.getIdentifier(), existingMetaboLightsStudy.getPublications().get(0).getTitle());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    ProcessingCertificate processTitle(Study study) {
        ProcessingCertificate certificate = getNewCertificate();
        certificate.setAccession(study.getAccession());
        if (study.getTitle() != null && !study.getTitle().isEmpty()) {
            try {
                this.updateService.updateTitle(study.getAccession(), study.getTitle());
                certificate.setMessage(getSuccessMessage("title"));
                certificate.setProcessingStatus(ProcessingStatusEnum.Submitted);
            } catch (Exception e) {
                certificate.setMessage("Error saving title : " + e.getMessage());
            }
        } else {
            certificate.setMessage(getWarningMessage("title"));
        }
        return certificate;
    }

    ProcessingCertificate processDescription(Study study) {
        ProcessingCertificate certificate = getNewCertificate();
        certificate.setAccession(study.getAccession());
        if (study.getDescription() != null && !study.getDescription().isEmpty()) {
            try {
                this.updateService.updateDescription(study.getAccession(), study.getDescription());
                certificate.setMessage(getSuccessMessage("description"));
                certificate.setProcessingStatus(ProcessingStatusEnum.Submitted);
            } catch (Exception e) {
                certificate.setMessage("Error saving description : " + e.getMessage());
            }
        } else {
            certificate.setMessage(getWarningMessage("description"));
        }
        return certificate;
    }


    ProcessingCertificate processStudyFactors(Study study, uk.ac.ebi.subs.metabolights.model.Study mlStudy) {
        ProcessingCertificate certificate = getNewCertificate();
        certificate.setAccession(study.getAccession());
        if (!AgentProcessorUtils.isPresent(study, StudyAttributes.STUDY_FACTORS)) {
            certificate.setMessage(getWarningMessage("factors"));
            return certificate;
        }
        try {
            if (!AgentProcessorUtils.containsValue(mlStudy.getFactors())) {
                this.postService.addStudyFactors(study.getAccession(), (List<Attribute>) study.getAttributes().get(StudyAttributes.STUDY_FACTORS));
            } else {
                for (Attribute factorAttribute : study.getAttributes().get(StudyAttributes.STUDY_FACTORS)) {
                    if (AgentProcessorUtils.isValid(factorAttribute.getValue())) {
                        if (AgentProcessorUtils.alreadyPresent(mlStudy.getFactors(), factorAttribute.getValue())) {
                            this.updateService.updateFactor(study.getAccession(), factorAttribute);
                        } else {
                            this.postService.addFactor(study.getAccession(), factorAttribute);
                        }
                    }
                }
                for (Factor factor : mlStudy.getFactors()) {
                    /*
                     Delete factors not present in USI attributes
                     */
                    if (!AgentProcessorUtils.alreadyPresent((List) study.getAttributes().get(StudyAttributes.STUDY_FACTORS), factor)) {
                        this.deletionService.deleteFactor(study.getAccession(), factor.getFactorName());
                    }
                }
            }
            certificate.setMessage(getSuccessMessage("factors"));
            certificate.setProcessingStatus(ProcessingStatusEnum.Submitted);
        } catch (Exception e) {
            certificate.setMessage("Error saving factors : " + e.getMessage());
        }
        return certificate;
    }

    ProcessingCertificate processStudyDescriptors(Study study, uk.ac.ebi.subs.metabolights.model.Study mlStudy) {
        ProcessingCertificate certificate = getNewCertificate();
        certificate.setAccession(study.getAccession());
        if (!AgentProcessorUtils.isPresent(study, StudyAttributes.STUDY_DESCRIPTORS)) {
            certificate.setMessage(getWarningMessage("descriptors"));
            return certificate;
        }
        try {
            if (!AgentProcessorUtils.containsValue(mlStudy.getStudyDesignDescriptors())) {
                this.postService.addStudyDesignDescriptors(study.getAccession(), (List<Attribute>) study.getAttributes().get(StudyAttributes.STUDY_DESCRIPTORS));
            } else {
                for (Attribute descriptorAttribute : study.getAttributes().get(StudyAttributes.STUDY_DESCRIPTORS)) {
                    if (AgentProcessorUtils.isValid(descriptorAttribute.getValue())) {
                        if (AgentProcessorUtils.alreadyHas(mlStudy.getStudyDesignDescriptors(), descriptorAttribute.getValue())) {
                            this.updateService.updateDescriptor(study.getAccession(), descriptorAttribute);
                        } else {
                            this.postService.addDescriptor(study.getAccession(), descriptorAttribute);
                        }
                    }
                }
                for (OntologyModel descriptor : mlStudy.getStudyDesignDescriptors()) {
                    /*
                     Delete study descriptors not present in USI attributes
                     */
                    if (!AgentProcessorUtils.alreadyPresent((List) study.getAttributes().get(StudyAttributes.STUDY_DESCRIPTORS), descriptor)) {
                        this.deletionService.deleteDescriptor(study.getId(), descriptor.getAnnotationValue());
                    }
                }
            }
            certificate.setMessage(getSuccessMessage("descriptors"));
            certificate.setProcessingStatus(ProcessingStatusEnum.Submitted);
        } catch (Exception e) {
            certificate.setMessage("Error saving factors : " + e.getMessage());
        }
        return certificate;
    }

    ProcessingCertificate processContacts(Study study, Project project, uk.ac.ebi.subs.metabolights.model.Study mlStudy) {
        ProcessingCertificate certificate = getNewCertificate();
        certificate.setAccession(study.getAccession());
        if (!AgentProcessorUtils.containsValue(project.getContacts())) {
            certificate.setMessage(getWarningMessage("contacts"));
            return certificate;
        }
        try {
            if (!AgentProcessorUtils.containsValue(mlStudy.getPeople())) {
                this.postService.addContacts(study.getAccession(), project.getContacts());
            } else {
                for (uk.ac.ebi.subs.data.component.Contact contact : project.getContacts()) {
                    if (AgentProcessorUtils.isValid(contact.getEmail())) {
                        if (AgentProcessorUtils.alreadyHas(mlStudy.getPeople(), contact)) {
                            this.updateService.updateContact(study.getAccession(), contact);
                        } else {
                            this.postService.addContacts(study.getAccession(), Arrays.asList(contact));
                        }
                    }
                }

                for (uk.ac.ebi.subs.metabolights.model.Contact contact : mlStudy.getPeople()) {
                    if (!AgentProcessorUtils.alreadyHas(project.getContacts(), contact)) {
                        this.deletionService.deleteDescriptor(study.getId(), contact.getEmail());
                    }
                }

            }
            certificate.setMessage(getSuccessMessage("contacts"));
            certificate.setProcessingStatus(ProcessingStatusEnum.Submitted);
        } catch (Exception e) {
            certificate.setMessage("Error saving contacts : " + e.getMessage());
        }
        return certificate;
    }

    ProcessingCertificate processPublications(Study study, Project project, uk.ac.ebi.subs.metabolights.model.Study mlStudy) {
        ProcessingCertificate certificate = getNewCertificate();
        certificate.setAccession(study.getAccession());
        if (!AgentProcessorUtils.containsValue(project.getPublications())) {
            certificate.setMessage(getWarningMessage("publications"));
            return certificate;
        }
        try {
            if (!AgentProcessorUtils.containsValue(mlStudy.getPublications())) {
                this.postService.addPublications(study.getAccession(), project.getPublications());
            } else {
                for (uk.ac.ebi.subs.data.component.Publication publication : project.getPublications()) {
                    if (AgentProcessorUtils.isValid(publication.getArticleTitle())) {
                        if (AgentProcessorUtils.alreadyHas(mlStudy.getPublications(), publication)) {
                            this.updateService.updatePublication(study.getAccession(), publication);
                        } else {
                            this.postService.add(study.getAccession(), publication);
                        }
                    }
                }
                for (uk.ac.ebi.subs.metabolights.model.Publication publication : mlStudy.getPublications()) {
                    if (!AgentProcessorUtils.alreadyHas(project.getPublications(), publication)) {
                        this.deletionService.deletePublication(study.getId(), publication.getTitle());
                    }
                }
            }
            certificate.setMessage(getSuccessMessage("publications"));
            certificate.setProcessingStatus(ProcessingStatusEnum.Submitted);
        } catch (Exception e) {
            certificate.setMessage("Error saving publications : " + e.getMessage());
        }
        return certificate;
    }

    ProcessingCertificate processProtocols(Study study, List<Protocol> protocols, uk.ac.ebi.subs.metabolights.model.Study mlStudy) {
        ProcessingCertificate certificate = getNewCertificate();
        certificate.setAccession(study.getAccession());
        if (!AgentProcessorUtils.containsValue(protocols)) {
            certificate.setMessage(getWarningMessage("protocols"));
            return certificate;
        }
        try {
            if (!AgentProcessorUtils.containsValue(mlStudy.getProtocols())) {
                this.postService.addStudyProtocols(study.getAccession(), protocols);
            } else {
                for (Protocol protocol : protocols) {
                    if (AgentProcessorUtils.isValid(protocol.getTitle())) {
                        if (AgentProcessorUtils.alreadyHas(mlStudy.getProtocols(), protocol)) {
                            this.updateService.updateProtocol(study.getAccession(), protocol);
                        } else {
                            this.postService.add(study.getAccession(), protocol);
                        }
                    }
                }
            }
            certificate.setMessage(getSuccessMessage("protocols"));
            certificate.setProcessingStatus(ProcessingStatusEnum.Submitted);
        } catch (Exception e) {
            certificate.setMessage("Error saving protocols : " + e.getMessage());
        }
        return certificate;
    }

    private ProcessingCertificate processSamples(Study study, List<Sample> samples, String sampleFileToUpdate, boolean isNewSubmission) {
        ProcessingCertificate certificate = getNewCertificate();
        certificate.setAccession(study.getAccession());
        if (!AgentProcessorUtils.containsValue(samples)) {
            certificate.setMessage(getWarningMessage("samples"));
            return certificate;
        }
        if (sampleFileToUpdate.isEmpty()) {
            certificate.setMessage("Something went wrong while trying to access the existing metabolights study files. Unable to update samples");
            return certificate;
        }
        //  this.postService.addSamples(samples, study.getAccession(), sampleFileToUpdate);
        //   this.updateService.updateSamples(samples, study.getAccession(), sampleFileToUpdate);

        MetaboLightsTable sampleTable = this.fetchService.getMetaboLightsDataTable(study.getAccession(), sampleFileToUpdate);

        if (isNewSubmission) {
            try {
                this.postService.addSamples(samples, study.getAccession(), sampleFileToUpdate, sampleTable.getHeader());
            } catch (Exception e) {
                certificate.setMessage("Error saving samples : " + e.getMessage());
            }
        } else {
            try {
//                MetaboLightsTable sampleTable = this.fetchService.getMetaboLightsDataTable(study.getAccession(), sampleFileToUpdate);
                Map<String, List<Sample>> samplesToAddAndUpdate = AgentProcessorUtils.getSamplesToAddAndUpdate(samples, sampleTable);
                this.updateService.updateSamples(samplesToAddAndUpdate.get("update"), study.getAccession(), sampleFileToUpdate, sampleTable.getHeader());
                this.postService.addSamples(samplesToAddAndUpdate.get("add"), study.getAccession(), sampleFileToUpdate, sampleTable.getHeader());
                /*
                Delete sample rows not present in submission's sample list
                 */
                List<Integer> sampleIndexesToDelete = AgentProcessorUtils.getSamplesIndexesToDelete(samples, sampleTable);
                if (sampleIndexesToDelete.size() > 0) {
                    this.deletionService.deleteTableRows(study.getAccession(), sampleFileToUpdate, sampleIndexesToDelete);
                }

            } catch (Exception e) {
                certificate.setMessage("Error saving samples : " + e.getMessage());
            }
        }
        return certificate;
    }

    private ProcessingCertificate processAssays(Study study, List<uk.ac.ebi.subs.data.submittable.Assay> assays, boolean isNewSubmission, List<String> assayFileNames) {
        ProcessingCertificate certificate = getNewCertificate();
        certificate.setAccession(study.getAccession());
        if (!AgentProcessorUtils.containsValue(assays)) {
            certificate.setMessage(getWarningMessage("assays"));
            return certificate;
        }
        //todo if new submission extract attributes from assay to create new template
        //todo handle multiple assays and other assay types
        // this is assuming single NMR assay
        if (isNewSubmission) {
            try {
                for (uk.ac.ebi.subs.data.submittable.Assay assay : assays) {
                    if (AgentProcessorUtils.getTechnologyType(assay).equalsIgnoreCase("NMR")) {
                        NewMetabolightsAssay newMetabolightsAssay = AgentProcessorUtils.generateNewNMRAssay();
                        HttpStatus status = this.postService.addNewAssay(newMetabolightsAssay, study.getAccession());
                        if (status.is2xxSuccessful()) {
                            StudyFiles studyFiles = this.fetchService.getStudyFiles(study.getAccession());
                            List<String> assayFiles = AgentProcessorUtils.getAssayFileName(studyFiles);
                            if (assayFiles.size() == 1) {
                                MetaboLightsTable assayTable = this.fetchService.getMetaboLightsDataTable(study.getAccession(), assayFiles.get(0));
                                this.postService.addAssayRows(assays, study.getAccession(), assayFiles.get(0), assayTable.getHeader());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                certificate.setMessage("Error saving assays : " + e.getMessage());
            }
        } else {
            try {
                if (assayFileNames.size() == 1) {
                    MetaboLightsTable assayTable = this.fetchService.getMetaboLightsDataTable(study.getAccession(), assayFileNames.get(0));
                    this.postService.addAssayRows(assays, study.getAccession(), assayFileNames.get(0), assayTable.getHeader());
                }
            } catch (Exception e) {
                certificate.setMessage("Error saving assays : " + e.getMessage());
            }
        }
        // todo if not new get matching assay file names using attributes that was used to create template and then do row updates
        // todo - decide how to store the created assay file name in the USI for futher updates. This might be tricky in case of multiple assay files.
        // todo - decide on logic how to select unique assay ROWS 
        // todo - implement logic, to select rows not needed anymore in the ML table, and call delete row method
        return certificate;
    }

    private void deleteDefaultRow(String accession, String sampleFileToUpdate) {
        /*
         * Sample file have default one row. Remove it using index 0
         */
        List<Integer> sampleRowsToDelete = new ArrayList<>();
        sampleRowsToDelete.add(new Integer(0));
        try {
            this.deletionService.deleteTableRows(accession, sampleFileToUpdate, sampleRowsToDelete);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    private void update(List<ProcessingCertificate> processingCertificateList, ProcessingCertificate certificate) {
        if (hasValue(certificate)) {
            processingCertificateList.add(certificate);
        }
    }

    private boolean hasValue(ProcessingCertificate processingCertificate) {
        return processingCertificate != null;
    }

    private ProcessingCertificate getNewCertificate() {
        ProcessingCertificate processingCertificate = new ProcessingCertificate();
        processingCertificate.setArchive(Archive.Metabolights);
        return processingCertificate;
    }

    private String getWarningMessage(String object) {
        return "No Study " + object + " found";
    }

    private String getSuccessMessage(String object) {
        return "Study " + object + " submitted successfully";
    }
}
