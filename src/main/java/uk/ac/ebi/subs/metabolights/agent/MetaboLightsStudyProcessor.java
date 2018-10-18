package uk.ac.ebi.subs.metabolights.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;

import uk.ac.ebi.subs.metabolights.model.StudyAttributes;
import uk.ac.ebi.subs.metabolights.services.DeletionService;
import uk.ac.ebi.subs.metabolights.services.FetchService;
import uk.ac.ebi.subs.metabolights.services.PostService;
import uk.ac.ebi.subs.metabolights.services.UpdateService;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.List;
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
        uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy = getStudyBy(study.getAccession(), isNewSubmission);

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
        return processingCertificateList;
    }


    private uk.ac.ebi.subs.metabolights.model.Study getStudyBy(String accession, boolean isNewSubmission) {
        try {
            uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy = this.fetchService.getStudy(accession);
            if (isNewSubmission) {
                resetDummyValuesIn(existingMetaboLightsStudy);
                existingMetaboLightsStudy = this.fetchService.getStudy(accession);
                return existingMetaboLightsStudy;
            } else {
                return existingMetaboLightsStudy;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    private void resetDummyValuesIn(uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy) {
        resetContacts(existingMetaboLightsStudy);
        resetPublications(existingMetaboLightsStudy);
    }

    private void resetContacts(uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy) {
        if (existingMetaboLightsStudy.getPeople() != null && existingMetaboLightsStudy.getPeople().size() == 1) {
            try {
                this.deletionService.deleteContact(existingMetaboLightsStudy.getIdentifier(), existingMetaboLightsStudy.getPeople().get(0));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void resetPublications(uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy) {
        if (existingMetaboLightsStudy.getPublications() != null && existingMetaboLightsStudy.getPublications().size() == 1) {
            try {
                this.deletionService.deletePublication(existingMetaboLightsStudy.getIdentifier(), existingMetaboLightsStudy.getPublications().get(0));
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
                for (Contact contact : project.getContacts()) {
                    if (AgentProcessorUtils.isValid(contact.getEmail())) {
                        if (AgentProcessorUtils.alreadyHas(mlStudy.getPeople(), contact)) {
                            this.updateService.updateContact(study.getAccession(), contact);
                        } else {
                            this.postService.add(study.getAccession(), contact);
                        }
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
                for (Publication publication : project.getPublications()) {
                    if (AgentProcessorUtils.isValid(publication.getArticleTitle())) {
                        if (AgentProcessorUtils.alreadyHas(mlStudy.getPublications(), publication)) {
                            this.updateService.updatePublication(study.getAccession(), publication);
                        } else {
                            this.postService.add(study.getAccession(), publication);
                        }
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
