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
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Topics;
import uk.ac.ebi.subs.metabolights.services.FetchService;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.processing.UpdatedSamplesEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MetaboLightsStudyProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MetaboLightsStudyProcessor.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    //todo Autowire? - call python API services for CRUD operations

    @Autowired
    ProcessingCertificateGenerator certificatesGenerator;

    @Autowired
    FetchService fetchService;

    @Autowired
    public MetaboLightsStudyProcessor(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter, FetchService fetchService) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
        this.fetchService = fetchService;
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

    ProcessingCertificateEnvelope processStudyInSubmission(SubmissionEnvelope submissionEnvelope) {
        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();
        if (submissionEnvelope.getStudies() != null && submissionEnvelope.getStudies().size() > 0) {
            for (Study study : submissionEnvelope.getStudies()) {
                ProcessingCertificate processingCertificate = new ProcessingCertificate();
                processingCertificate.setArchive(Archive.Metabolights);
                if(!study.isAccessioned()){
                    String accession = this.fetchService.createNewStudyAndGetAccession();
                    study.setAccession(accession);
                    processingCertificate.setAccession(accession);
                    processingCertificate.setProcessingStatus(ProcessingStatusEnum.Processing);
                } else{
                    processingCertificate.setAccession(study.getAccession());
                }

                processingCertificateList.add(processingCertificate);
            }
        }
        return new ProcessingCertificateEnvelope(submissionEnvelope.getSubmission().getId(), processingCertificateList);
    }


}
