package uk.ac.ebi.subs.metabolights.agent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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
import uk.ac.ebi.subs.metabolights.converters.Utilities;
import uk.ac.ebi.subs.metabolights.services.FetchService;
import uk.ac.ebi.subs.metabolights.services.PostService;
import uk.ac.ebi.subs.metabolights.services.UpdateService;
import uk.ac.ebi.subs.metabolights.validator.ValidationTestUtils;
import uk.ac.ebi.subs.metabolights.validator.ValidationUtils;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

//@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = {
        MetaboLightsAgentApplication.class} )
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class MetaboLightsStudyProcessorTest {

//
//    @Mock
//    FetchService fetchServiceMock;
//
//    @Mock
//    ProcessingCertificateGenerator processingCertificateGenerator;
//
//    @Mock
//    RabbitMessagingTemplate rabbitMessagingTemplate;
//
//    @InjectMocks
//    MetaboLightsStudyProcessor metaboLightsStudyProcessorMock;
//
    @Autowired
    MetaboLightsStudyProcessor metaboLightsStudyProcessor;

    @MockBean
    RabbitMessagingTemplate rabbitMessagingTemplate;
    @MockBean(name = "messageConverter")
    MessageConverter messageConverter;


//    @Test
//    public void processStudyInSubmission() {
//    }
//
//    @Test
//    public void testCreateNewStudyAndGetAccessionWithMock() {
//        when(fetchServiceMock.createNewStudyAndGetAccession()).thenReturn("MTBLS_DEV122");
//        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();
//        Submission submission = new Submission();
//        submission.setId("test-0");
//        submissionEnvelope.setSubmission(submission);
//        Study study = new Study();
//        Project project = ValidationTestUtils.getProjectWithContactsAndPublications().getBaseSubmittable();
//        submissionEnvelope.getStudies().add(study);
//        submissionEnvelope.getProjects().add(project);
//        ProcessingCertificateEnvelope processingCertificateEnvelope = metaboLightsStudyProcessorMock.processStudyInSubmission(submissionEnvelope);
//        assertEquals(ProcessingStatusEnum.Processing, processingCertificateEnvelope.getProcessingCertificates().get(0).getProcessingStatus());
//    }

    @Test
    public void testCreateNewStudyAndGetAccession() {
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();
        Submission submission = new Submission();
        submission.setId("test-0");
        submissionEnvelope.setSubmission(submission);
        Study study = new Study();
        study.setTitle("This is test title");
        study.setDescription("This is test description");
        Project project = ValidationTestUtils.getProjectWithContactsAndPublications().getBaseSubmittable();
        submissionEnvelope.getStudies().add(study);
        submissionEnvelope.getProjects().add(project);
        ProcessingCertificateEnvelope processingCertificateEnvelope = metaboLightsStudyProcessor.processStudyInSubmission(submissionEnvelope);
        assertEquals(ProcessingStatusEnum.Processing, processingCertificateEnvelope.getProcessingCertificates().get(0).getProcessingStatus());
    }
}