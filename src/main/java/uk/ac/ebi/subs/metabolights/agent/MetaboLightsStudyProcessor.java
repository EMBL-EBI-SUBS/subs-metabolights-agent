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
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.*;

import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.metabolights.model.*;
import uk.ac.ebi.subs.metabolights.services.*;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.processing.fileupload.UploadedFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

//    @Autowired
//    FileMoveService fileMoveService;

//    @Value("${spring.profiles.active:dev}")
//    private String activeProfile;


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
                ProcessingCertificate processingCertificate = new ProcessingCertificate();
                processingCertificate.setProcessingStatus(ProcessingStatusEnum.Error);
                processingCertificate.setMessage(submissionEnvelope.getSubmission().getId() + " has no biostudies accession. This will prevent sending " +
                        "updates to metabolights. Please provide biostudies accession with this submission.");
                processingCertificateList.add(processingCertificate);
                return new ProcessingCertificateEnvelope(
                        submissionEnvelope.getSubmission().getId(), processingCertificateList, submissionEnvelope.getJWTToken());
            } else {
                String mlStudyID = this.fetchService.getMLStudyID(study.getAccession());
                if (AgentProcessorUtils.biostudiesIsAlreadyLinkedWith(mlStudyID)) {
                    AgentProcessorUtils.addMLStudyForRuntimeUse(mlStudyID, study);
                    processingCertificateList.addAll(processMetaData(study, submissionEnvelope, false));
                } else {
                    return createNewMetaboLightsStudy(study, submissionEnvelope);
                }
            }
        }
        return new ProcessingCertificateEnvelope(
                submissionEnvelope.getSubmission().getId(), processingCertificateList, submissionEnvelope.getJWTToken());
    }

    ProcessingCertificateEnvelope createNewMetaboLightsStudy(Study study, SubmissionEnvelope submissionEnvelope) {
//        moveUploadedFilesToArchive(submissionEnvelope);
//        injectPathAndChecksum(submissionEnvelope);

        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();
        ProcessingCertificate processingCertificate = getNewCertificate();
        try {
            String metabolightsStudyID = this.fetchService.createNewStudyAndGetAccession();
            processingCertificate.setAccession(study.getAccession());
            processingCertificate.setMessage("Study successfully accessioned in metabolights - " + metabolightsStudyID);
            AgentProcessorUtils.addMLStudyForRuntimeUse(metabolightsStudyID, study);
            this.postService.addBioStudiesAccession(metabolightsStudyID, study.getAccession());
        } catch (Exception e) {
            processingCertificate.setMessage("Error creating new study : " + e.getMessage());
            processingCertificateList.add(processingCertificate);
            return new ProcessingCertificateEnvelope(
                    submissionEnvelope.getSubmission().getId(), processingCertificateList, submissionEnvelope.getJWTToken());
        }
        processingCertificateList.addAll(processMetaData(study, submissionEnvelope, true));
        processingCertificate.setProcessingStatus(ProcessingStatusEnum.Submitted);
        processingCertificateList.add(processingCertificate);
        return new ProcessingCertificateEnvelope(
                submissionEnvelope.getSubmission().getId(), processingCertificateList, submissionEnvelope.getJWTToken());
    }

    List<ProcessingCertificate> processMetaData(Study study, SubmissionEnvelope submissionEnvelope, boolean isNewSubmission) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();

        uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy = getStudyBy(ServiceUtils.getMLstudyId(study));
        StudyFiles studyFiles = this.fetchService.getStudyFiles(ServiceUtils.getMLstudyId(study));
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
            //todo handle multiple projects
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

        update(processingCertificateList, processAssaysAndAssayData(study, submissionEnvelope.getAssays(), submissionEnvelope.getAssayData(), isNewSubmission, assayFileNames));

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
                this.updateService.updateTitle(ServiceUtils.getMLstudyId(study), study.getTitle());
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
                this.updateService.updateDescription(ServiceUtils.getMLstudyId(study), study.getDescription());
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
                this.postService.addStudyFactors(ServiceUtils.getMLstudyId(study), (List<Attribute>) study.getAttributes().get(StudyAttributes.STUDY_FACTORS));
            } else {
                for (Attribute factorAttribute : study.getAttributes().get(StudyAttributes.STUDY_FACTORS)) {
                    if (AgentProcessorUtils.isValid(factorAttribute.getValue())) {
                        if (AgentProcessorUtils.alreadyPresent(mlStudy.getFactors(), factorAttribute.getValue())) {
                            this.updateService.updateFactor(ServiceUtils.getMLstudyId(study), factorAttribute);
                        } else {
                            this.postService.addFactor(ServiceUtils.getMLstudyId(study), factorAttribute);
                        }
                    }
                }
                for (Factor factor : mlStudy.getFactors()) {
                    /*
                     Delete factors not present in USI attributes
                     */
                    if (!AgentProcessorUtils.alreadyPresent((List) study.getAttributes().get(StudyAttributes.STUDY_FACTORS), factor)) {
                        this.deletionService.deleteFactor(ServiceUtils.getMLstudyId(study), factor.getFactorName());
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
                this.postService.addStudyDesignDescriptors(ServiceUtils.getMLstudyId(study), (List<Attribute>) study.getAttributes().get(StudyAttributes.STUDY_DESCRIPTORS));
            } else {
                for (Attribute descriptorAttribute : study.getAttributes().get(StudyAttributes.STUDY_DESCRIPTORS)) {
                    if (AgentProcessorUtils.isValid(descriptorAttribute.getValue())) {
                        if (AgentProcessorUtils.alreadyHas(mlStudy.getStudyDesignDescriptors(), descriptorAttribute.getValue())) {
                            this.updateService.updateDescriptor(ServiceUtils.getMLstudyId(study), descriptorAttribute);
                        } else {
                            this.postService.addDescriptor(ServiceUtils.getMLstudyId(study), descriptorAttribute);
                        }
                    }
                }
                for (OntologyModel descriptor : mlStudy.getStudyDesignDescriptors()) {
                    /*
                     Delete study descriptors not present in USI attributes
                     */
                    if (!AgentProcessorUtils.alreadyPresent((List) study.getAttributes().get(StudyAttributes.STUDY_DESCRIPTORS), descriptor)) {
                        this.deletionService.deleteDescriptor(ServiceUtils.getMLstudyId(study), descriptor.getAnnotationValue());
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
                this.postService.addContacts(ServiceUtils.getMLstudyId(study), project.getContacts());
            } else {
                for (uk.ac.ebi.subs.data.component.Contact contact : project.getContacts()) {
                    if (AgentProcessorUtils.isValid(contact.getEmail())) {
                        if (AgentProcessorUtils.alreadyHas(mlStudy.getPeople(), contact)) {
                            this.updateService.updateContact(ServiceUtils.getMLstudyId(study), contact);
                        } else {
                            this.postService.addContacts(ServiceUtils.getMLstudyId(study), Arrays.asList(contact));
                        }
                    }
                }

                for (uk.ac.ebi.subs.metabolights.model.Contact contact : mlStudy.getPeople()) {
                    if (!AgentProcessorUtils.alreadyHas(project.getContacts(), contact)) {
                        this.deletionService.deleteContact(ServiceUtils.getMLstudyId(study), contact.getEmail());
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
                this.postService.addPublications(ServiceUtils.getMLstudyId(study), project.getPublications());
            } else {
                for (uk.ac.ebi.subs.data.component.Publication publication : project.getPublications()) {
                    if (AgentProcessorUtils.isValid(publication.getArticleTitle())) {
                        if (AgentProcessorUtils.alreadyHas(mlStudy.getPublications(), publication)) {
                            this.updateService.updatePublication(ServiceUtils.getMLstudyId(study), publication);
                        } else {
                            this.postService.add(ServiceUtils.getMLstudyId(study), publication);
                        }
                    }
                }
                for (uk.ac.ebi.subs.metabolights.model.Publication publication : mlStudy.getPublications()) {
                    if (!AgentProcessorUtils.alreadyHas(project.getPublications(), publication)) {
                        this.deletionService.deletePublication(ServiceUtils.getMLstudyId(study), publication.getTitle());
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
                this.postService.addStudyProtocols(ServiceUtils.getMLstudyId(study), protocols);
            } else {
                for (Protocol protocol : protocols) {
                    if (AgentProcessorUtils.isValid(protocol.getTitle())) {
                        if (AgentProcessorUtils.alreadyHas(mlStudy.getProtocols(), protocol)) {
                            this.updateService.updateProtocol(ServiceUtils.getMLstudyId(study), protocol);
                        } else {
                            this.postService.add(ServiceUtils.getMLstudyId(study), protocol);
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
        //  this.postService.addSamples(samples, ServiceUtils.getMLstudyId(study), sampleFileToUpdate);
        //   this.updateService.updateSamples(samples, ServiceUtils.getMLstudyId(study), sampleFileToUpdate);

        MetaboLightsTableResult sampleTable = this.fetchService.getMetaboLightsSampleDataTable(ServiceUtils.getMLstudyId(study), sampleFileToUpdate);

        if (isNewSubmission) {
            try {
                this.postService.addSampleRows(samples, ServiceUtils.getMLstudyId(study), sampleFileToUpdate, sampleTable.getHeader());
            } catch (Exception e) {
                certificate.setMessage("Error saving samples : " + e.getMessage());
            }
        } else {
            try {
//                MetaboLightsTable sampleTable = this.fetchService.getMetaboLightsDataTable(ServiceUtils.getMLstudyId(study), sampleFileToUpdate);
                Map<String, List<Sample>> samplesToAddAndUpdate = AgentProcessorUtils.getSamplesToAddAndUpdate(samples, sampleTable);
                if (samplesToAddAndUpdate.get("update").size() > 0) {
                    this.updateService.updateSamples(samplesToAddAndUpdate.get("update"), ServiceUtils.getMLstudyId(study), sampleFileToUpdate, sampleTable.getHeader());
                }
                if (samplesToAddAndUpdate.get("add").size() > 0) {
                    this.postService.addSampleRows(samplesToAddAndUpdate.get("add"), ServiceUtils.getMLstudyId(study), sampleFileToUpdate, sampleTable.getHeader());
                }
                /*
                Delete sample rows not present in submission's sample list
                 */
                List<Integer> sampleIndexesToDelete = AgentProcessorUtils.getSamplesIndexesToDelete(samples, sampleTable);
                if (sampleIndexesToDelete.size() > 0) {
                    this.deletionService.deleteTableRows(ServiceUtils.getMLstudyId(study), sampleFileToUpdate, sampleIndexesToDelete);
                }

            } catch (Exception e) {
                certificate.setMessage("Error saving samples : " + e.getMessage());
            }
        }
        return certificate;
    }

    private ProcessingCertificate processAssaysAndAssayData(Study study, List<uk.ac.ebi.subs.data.submittable.Assay> assays, List<AssayData> assayData, boolean isNewSubmission, List<String> assayFileNames) {
        ProcessingCertificate certificate = getNewCertificate();
        certificate.setAccession(study.getAccession());
        if (!AgentProcessorUtils.containsValue(assays)) {
            certificate.setMessage(getWarningMessage("assays"));
            return certificate;
        }
        AgentProcessorUtils.combine(assays, assayData);
        //todo if new submission extract attributes from assay to create new template
        //todo handle multiple assays and other assay types
        // this is assuming single NMR assay

        if (isNewSubmission) {
            try {
                createNewAssayAndPostAllAssayRows(study, assays);
            } catch (Exception e) {
                certificate.setMessage("Error saving assays : " + e.getMessage());
            }
        } else {
            try {
                if (assayFileNames.size() > 0) {
                    //todo pick assayfile by crosschecking the alias. At the moment only one assay is considered.
                    MetaboLightsTable assayTable = this.fetchService.getMetaboLightsDataTable(ServiceUtils.getMLstudyId(study), assayFileNames.get(0));
                    Map<String, List<uk.ac.ebi.subs.data.submittable.Assay>> assayRowsToAddAndUpdate = AgentProcessorUtils.getAssayRowsToAddAndUpdate(assays, assayTable);
                    if (assayRowsToAddAndUpdate.get("update").size() > 0) {
                        this.updateService.updateAssays(assayRowsToAddAndUpdate.get("update"), ServiceUtils.getMLstudyId(study), assayFileNames.get(0), assayTable.getHeader());
                    }
                    if (assayRowsToAddAndUpdate.get("add").size() > 0) {
                        this.postService.addAssayRows(assayRowsToAddAndUpdate.get("add"), ServiceUtils.getMLstudyId(study), assayFileNames.get(0), assayTable.getHeader());
                    }
                /*
                Delete assay rows not present in submission's assay list
                 */
                    List<Integer> assayRowIndexesToDelete = AgentProcessorUtils.getAssayRowIndexesToDelete(assays, assayTable);
                    if (assayRowIndexesToDelete.size() > 0) {
                        this.deletionService.deleteTableRows(ServiceUtils.getMLstudyId(study), assayFileNames.get(0), assayRowIndexesToDelete);
                    }
                } else {
                    //create new assay when assay data comes in resubmission
                    createNewAssayAndPostAllAssayRows(study, assays);
                }
            } catch (Exception e) {
                certificate.setMessage("Error saving assays : " + e.getMessage());
            }
        }
        // todo if not new get matching assay file names using attributes that was used to create template and then do row updates
        // todo - decide how to store the created assay file name in the USI for futher updates. This might be tricky in case of multiple assay files.
        return certificate;
    }

    private void createNewAssayAndPostAllAssayRows(Study study, List<uk.ac.ebi.subs.data.submittable.Assay> assays) {
        //todo make sure we have a parameter that distinguishes rows from different assay spreadsheets. We have to create multiple assay files based on that info.
        // todo this is assuming NMR assay. Sort for other assay types
        NewMetabolightsAssay newMetabolightsAssay = AgentProcessorUtils.generateNewNMRAssay();
        HttpStatus status = this.postService.addNewAssay(newMetabolightsAssay, ServiceUtils.getMLstudyId(study));
        if (status.is2xxSuccessful()) {
            StudyFiles studyFiles = this.fetchService.getStudyFiles(ServiceUtils.getMLstudyId(study));
            List<String> assayFiles = AgentProcessorUtils.getAssayFileName(studyFiles);
            if (assayFiles.size() == 1) {
                MetaboLightsTable assayTable = this.fetchService.getMetaboLightsDataTable(ServiceUtils.getMLstudyId(study), assayFiles.get(0));
                this.postService.addAssayRows(assays, ServiceUtils.getMLstudyId(study), assayFiles.get(0), assayTable.getHeader());
            }
        }
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

//    private void moveUploadedFilesToArchive(SubmissionEnvelope submissionEnvelope) {
//        submissionEnvelope.getUploadedFiles().forEach(uploadedFile -> {
//            fileMoveService.moveFile(uploadedFile.getPath());
//        });
//    }
//
//    private void injectPathAndChecksum(SubmissionEnvelope submissionEnvelope) {
//        Map<String, UploadedFile> uploadedFileMap = filesByFilename(submissionEnvelope.getUploadedFiles());
//
//        Stream<File> assayDataFileStream = submissionEnvelope.getAssayData().stream().flatMap(ad -> ad.getFiles().stream());
//        Stream<File> analysisFileStream = submissionEnvelope.getAnalyses().stream().flatMap(a -> a.getFiles().stream());
//
//        Stream.concat(assayDataFileStream, analysisFileStream).forEach(file -> {
//            UploadedFile uploadedFile = uploadedFileMap.get(file.getName());
//            file.setChecksum(uploadedFile.getChecksum());
//            //todo configure active Profile equivalent
//            // file.setName(String.join("/", activeProfile, fileMoveService.getRelativeFilePath(uploadedFile.getPath())));
//            file.setName(String.join("/", fileMoveService.getRelativeFilePath(uploadedFile.getPath())));
//        });
//
//    }

    Map<String, UploadedFile> filesByFilename(List<UploadedFile> files) {
        Map<String, UploadedFile> filesByFilename = new HashMap<>();
        files.forEach(file -> filesByFilename.put(file.getFilename(), file));

        return filesByFilename;
    }

}
