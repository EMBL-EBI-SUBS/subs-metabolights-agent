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
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;

import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.StudyAttributes;
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
                processingCertificateList.addAll(processMetaData(true, study, submissionEnvelope));
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
            processingCertificate.setProcessingStatus(ProcessingStatusEnum.Processing);
        } catch (Exception e) {
            processingCertificate.setMessage("Error creating new study : " + e.getMessage());
            processingCertificateList.add(processingCertificate);
            return new ProcessingCertificateEnvelope(submissionEnvelope.getSubmission().getId(), processingCertificateList);
        }
        processingCertificateList.addAll(processMetaData(false, study, submissionEnvelope));
        return new ProcessingCertificateEnvelope(submissionEnvelope.getSubmission().getId(), processingCertificateList);
    }

    List<ProcessingCertificate> processMetaData(boolean update, Study study, SubmissionEnvelope submissionEnvelope) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();
        uk.ac.ebi.subs.metabolights.model.Study existingMetaboLightsStudy = this.fetchService.getStudy(study.getAccession());
        update(processingCertificateList, processTitle(study));
        update(processingCertificateList, processDescription(study));
        update(processingCertificateList, processStudyFactors(study, update));
        update(processingCertificateList, processStudyDescriptors(study, update));
        update(processingCertificateList, processProtocols(study, submissionEnvelope.getProtocols(), update));

        if (submissionEnvelope.getProjects() != null && submissionEnvelope.getProjects().size() > 0) {
            //todo handle multiple projects
            update(processingCertificateList, processContacts(study, submissionEnvelope.getProjects().get(0), update));
            update(processingCertificateList, processPublications(study, submissionEnvelope.getProjects().get(0), update));
        }
        return processingCertificateList;
    }

    ProcessingCertificate processTitle(Study study) {
        ProcessingCertificate certificate = null;
        if (study.getTitle() != null && !study.getTitle().isEmpty()) {
            try {
                this.updateService.updateTitle(study.getAccession(), study.getTitle());
            } catch (Exception e) {
                certificate = getNewCertificate();
                certificate.setAccession(study.getAccession());
                certificate.setMessage("Error saving title : " + e.getMessage());
            }
        } else {
            return newCertificateWithWarning(study.getAccession(), "title");
        }
        return certificate;
    }

    ProcessingCertificate processDescription(Study study) {
        ProcessingCertificate certificate = null;
        if (study.getDescription() != null && !study.getDescription().isEmpty()) {
            try {
                this.updateService.updateDescription(study.getAccession(), study.getDescription());
            } catch (Exception e) {
                certificate = getNewCertificate();
                certificate.setAccession(study.getAccession());
                certificate.setMessage("Error saving description : " + e.getMessage());
            }
        } else {
            return newCertificateWithWarning(study.getAccession(), "description");
        }
        return certificate;
    }

    ProcessingCertificate processStudyFactors(Study study, boolean update) {
        ProcessingCertificate certificate = null;
        if (!isPresent(study, StudyAttributes.STUDY_FACTORS)) {
            return newCertificateWithWarning(study.getAccession(), "factors");
        }
        try {
            if (update) {
                this.updateService.updateStudyFactors(study.getAccession(), (List<Attribute>) study.getAttributes().get(StudyAttributes.STUDY_FACTORS));
            } else {
                this.postService.addStudyFactors(study.getAccession(), (List<Attribute>) study.getAttributes().get(StudyAttributes.STUDY_FACTORS));
            }
        } catch (Exception e) {
            certificate = getNewCertificate();
            certificate.setAccession(study.getAccession());
            certificate.setMessage("Error saving factors : " + e.getMessage());
        }
        return certificate;
    }

    ProcessingCertificate processStudyFactors(Study study, uk.ac.ebi.subs.metabolights.model.Study mlStudy) {
        ProcessingCertificate certificate = null;
        if (!isPresent(study, StudyAttributes.STUDY_FACTORS)) {
            return newCertificateWithWarning(study.getAccession(), "factors");
        }
        try {
            if (!containsValue(mlStudy.getFactors())) {
                this.postService.addStudyFactors(study.getAccession(), (List<Attribute>) study.getAttributes().get(StudyAttributes.STUDY_FACTORS));
            } else {
                for (Attribute factorAttribute : study.getAttributes().get(StudyAttributes.STUDY_FACTORS)) {
                    if (alreadyPresent(mlStudy.getFactors(), factorAttribute.getValue())) {
                        this.updateService.updateFactor(study.getAccession(), factorAttribute);
                    } else {
                        this.postService.addFactor(study.getAccession(), factorAttribute);
                    }
                }
            }
        } catch (Exception e) {
            certificate = getNewCertificate();
            certificate.setAccession(study.getAccession());
            certificate.setMessage("Error saving factors : " + e.getMessage());
        }
        return certificate;
    }

    ProcessingCertificate processStudyDescriptors(Study study, boolean update) {
        ProcessingCertificate certificate = null;
        if (!isPresent(study, StudyAttributes.STUDY_DESCRIPTORS)) {
            return newCertificateWithWarning(study.getAccession(), "descriptors");
        }
        try {
            if (update) {
                this.updateService.updateStudyDesignDescriptors(study.getAccession(), (List<Attribute>) study.getAttributes().get(StudyAttributes.STUDY_DESCRIPTORS));
            } else {
                this.postService.addStudyDesignDescriptors(study.getAccession(), (List<Attribute>) study.getAttributes().get(StudyAttributes.STUDY_DESCRIPTORS));
            }
        } catch (Exception e) {
            certificate = getNewCertificate();
            certificate.setAccession(study.getAccession());
            certificate.setMessage("Error saving descriptors : " + e.getMessage());
        }
        return certificate;
    }

    ProcessingCertificate processContacts(Study study, Project project, boolean update) {
        ProcessingCertificate certificate = null;
        if (project.getContacts() == null || project.getContacts().isEmpty()) {
            return newCertificateWithWarning(study.getAccession(), "contacts");
        }
        try {
            //todo create new or update. keep track of submissions
            if (update) {
                this.updateService.updateContacts(study.getAccession(), project.getContacts());
            } else {
                this.postService.addContacts(study.getAccession(), project.getContacts());
            }
        } catch (Exception e) {
            certificate = getNewCertificate();
            certificate.setAccession(study.getAccession());
            certificate.setMessage("Error saving contacts : " + e.getMessage());
        }
        return certificate;
    }

    ProcessingCertificate processPublications(Study study, Project project, boolean update) {
        ProcessingCertificate certificate = null;
        if (project.getPublications() == null || project.getPublications().isEmpty()) {
            return newCertificateWithWarning(study.getAccession(), "publications");
        }
        try {
            //todo create new or update. keep track of submissions
            if (update) {
                this.updateService.updatePublications(study.getAccession(), project.getPublications());
            } else {
                this.postService.addPublications(study.getAccession(), project.getPublications());
            }
        } catch (Exception e) {
            certificate = getNewCertificate();
            certificate.setAccession(study.getAccession());
            certificate.setMessage("Error saving publications : " + e.getMessage());
        }
        return certificate;
    }

    ProcessingCertificate processProtocols(Study study, List<Protocol> protocols, boolean update) {
        ProcessingCertificate certificate = null;
        if (protocols == null || protocols.isEmpty()) {
            return newCertificateWithWarning(study.getAccession(), "protocols");
        }
        try {
            //todo create new or update. keep track of submissions
            if (update) {
                this.updateService.updateStudyProtocols(study.getAccession(), protocols);
            } else {
                this.postService.addStudyProtocols(study.getAccession(), protocols);
            }
        } catch (Exception e) {
            certificate = getNewCertificate();
            certificate.setAccession(study.getAccession());
            certificate.setMessage("Error saving protocols : " + e.getMessage());
        }
        return certificate;
    }

    private boolean isPresent(Study study, String attribute) {
        return study.getAttributes().get(attribute) != null && !study.getAttributes().get(attribute).isEmpty();
    }

    private void update(List<ProcessingCertificate> processingCertificateList, ProcessingCertificate certificate) {
        if (hasValue(certificate)) {
            processingCertificateList.add(certificate);
        }
    }

    private boolean containsValue(List entries) {
        return entries != null && entries.size() > 0;
    }

    private boolean alreadyPresent(List<Factor> factors, String factorAttributeName) {
        for (Factor factor : factors) {
            if (factor.getFactorName().equalsIgnoreCase(factorAttributeName)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasValue(ProcessingCertificate processingCertificate) {
        return processingCertificate != null;
    }

    private ProcessingCertificate getNewCertificate() {
        ProcessingCertificate processingCertificate = new ProcessingCertificate();
        processingCertificate.setArchive(Archive.Metabolights);
        return processingCertificate;
    }

    private ProcessingCertificate newCertificateWithWarning(String accession, String object) {
        ProcessingCertificate processingCertificate = getNewCertificate();
        processingCertificate.setAccession(accession);
        processingCertificate.setMessage("No Study " + object + " found");
        return processingCertificate;
    }
}
