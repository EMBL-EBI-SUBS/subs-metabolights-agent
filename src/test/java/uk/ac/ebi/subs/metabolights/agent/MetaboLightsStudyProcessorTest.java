package uk.ac.ebi.subs.metabolights.agent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.MetaboLightsAgentApplication;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.metabolights.validator.ValidationTestUtils;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import static org.junit.Assert.*;


@SpringBootTest(classes = {
        MetaboLightsAgentApplication.class})
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class MetaboLightsStudyProcessorTest {

    @Autowired
    MetaboLightsStudyProcessor metaboLightsStudyProcessor;

    @MockBean
    RabbitMessagingTemplate rabbitMessagingTemplate;
    @MockBean(name = "messageConverter")
    MessageConverter messageConverter;

    @Test
    public void testCreateNewStudyAndGetAccession() {
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();
        Submission submission = new Submission();
        submission.setId("test-0");
        submissionEnvelope.setSubmission(submission);
        Study study = new Study();
        study.setAccession("MTBLS_DEV2016");
        study.setTitle("This is test title");
        study.setDescription("This is test description");
        study.setAttributes(ValidationTestUtils.getStudyAttributes());
        Project project = ValidationTestUtils.getProjectWithContactsAndPublications().getBaseSubmittable();
        submissionEnvelope.getStudies().add(study);
        submissionEnvelope.getProjects().add(project);
        ProcessingCertificateEnvelope processingCertificateEnvelope = metaboLightsStudyProcessor.processStudy(submissionEnvelope);
        assertEquals(ProcessingStatusEnum.Processing, processingCertificateEnvelope.getProcessingCertificates().get(0).getProcessingStatus());
    }
}