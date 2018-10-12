package uk.ac.ebi.subs.metabolights.agent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.metabolights.services.FetchService;
import uk.ac.ebi.subs.metabolights.validator.ValidationTestUtils;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MockMetaboLightsStudyProcessorTest {

    @Mock
    FetchService fetchServiceMock;

    @Mock
    ProcessingCertificateGenerator processingCertificateGenerator;

    @Mock
    RabbitMessagingTemplate rabbitMessagingTemplate;

    @InjectMocks
    MetaboLightsStudyProcessor metaboLightsStudyProcessorMock;


    @Test
    public void testCreateNewStudyAndGetAccessionWithMock() {
        when(fetchServiceMock.createNewStudyAndGetAccession()).thenReturn("MTBLS_DEV122");
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();
        Submission submission = new Submission();
        submission.setId("test-0");
        submissionEnvelope.setSubmission(submission);
        Study study = new Study();
        study.setAccession("MTBLS_DEV122");
        Project project = ValidationTestUtils.getProjectWithContactsAndPublications().getBaseSubmittable();
        submissionEnvelope.getStudies().add(study);
        submissionEnvelope.getProjects().add(project);
        ProcessingCertificateEnvelope processingCertificateEnvelope = metaboLightsStudyProcessorMock.processStudy(submissionEnvelope);
        assertEquals(processingCertificateEnvelope.getProcessingCertificates().size(), 2);
    }


}
