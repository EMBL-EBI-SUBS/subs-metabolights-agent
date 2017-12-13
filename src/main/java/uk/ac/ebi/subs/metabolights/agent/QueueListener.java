package uk.ac.ebi.subs.metabolights.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Queues;
import uk.ac.ebi.subs.messaging.Topics;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.List;

@Service
public class QueueListener {
    private static final Logger logger = LoggerFactory.getLogger(QueueListener.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Autowired
    MetaboLightsStudyProcessor samplesProcessor;

    @Autowired
    ProcessingCertificateGenerator certificatesGenerator;

    @Autowired
    public QueueListener(RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
    }

    @RabbitListener(queues = Queues.METABOLIGHTS_AGENT)
    public void handleMetabolightsSubmission(SubmissionEnvelope envelope) {
        Submission submission = envelope.getSubmission();
        submission.setId("test-0");

        List<ProcessingCertificate> certificatesCompleted = new ArrayList<>();
        ProcessingCertificateEnvelope certificateEnvelopeCompleted = new ProcessingCertificateEnvelope(
                submission.getId(),
                certificatesCompleted
        );
        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBMISSION_AGENT_RESULTS, certificateEnvelopeCompleted);


        // Process samples
// //       List<ProcessingCertificate> certificatesCompleted = MetaboLightsStudyProcessor.processSamples(envelope);
//        List<ProcessingCertificate> certificatesCompleted = new ArrayList<>();
//        ProcessingCertificateEnvelope certificateEnvelopeCompleted = new ProcessingCertificateEnvelope(
//                submission.getId(),
//                certificatesCompleted
//        );
//        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBMISSION_AGENT_RESULTS, certificateEnvelopeCompleted);

        logger.info("Processed submission {}", submission.getId());
    }

    @RabbitListener(queues = Queues.METABOLIGHTS_SAMPLES_UPDATED)
    public void updateStudySamples(SubmissionEnvelope envelope) {
        Submission submission = envelope.getSubmission();
        //todo
        // update metabolights when sample info has been updated?
    }

}