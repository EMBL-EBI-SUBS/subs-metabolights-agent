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
import uk.ac.ebi.subs.metabolights.converters.USISampleToMLSample;
import uk.ac.ebi.subs.metabolights.converters.Utilities;
import uk.ac.ebi.subs.metabolights.model.Sample;
import uk.ac.ebi.subs.metabolights.model.SampleMap;
import uk.ac.ebi.subs.metabolights.model.StudyAttributes;
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
        study.setTitle("This is test title");
        study.setDescription("This is test description");
        study.setAttributes(ValidationTestUtils.getStudyAttributes());
        Project project = ValidationTestUtils.getProjectWithContactsAndPublications().getBaseSubmittable();
        submissionEnvelope.getStudies().add(study);
        submissionEnvelope.getProjects().add(project);
        submissionEnvelope.getProtocols().addAll(ValidationTestUtils.generateUSIProtocols());

        submissionEnvelope.getSamples().add(Utilities.getUSISampleFromDisc());
        submissionEnvelope.getAssays().add(Utilities.getUSIAssayFromDisc());
        submissionEnvelope.getAssayData().addAll(Utilities.generateUSIAssayDataForSingleAssay());
        submissionEnvelope.getStudies().get(0).getAttributes().remove(StudyAttributes.STUDY_FACTORS);
        submissionEnvelope.getStudies().get(0).getAttributes().put(StudyAttributes.STUDY_FACTORS, ValidationTestUtils.getStudyFactorsMatchingSampleTestFile());

        ProcessingCertificateEnvelope processingCertificateEnvelope = metaboLightsStudyProcessor.processStudy(submissionEnvelope);
        assertEquals(9, processingCertificateEnvelope.getProcessingCertificates().size());
    }

    @Test
    public void testUpdatingExistingStudy() {
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();
        Submission submission = new Submission();
        submission.setId("test-0");
        submissionEnvelope.setSubmission(submission);
        Study study = new Study();
        study.setTitle("This is test title");
        study.setDescription("This is test description");
        study.setAccession("MTBLS_DEV2346");
        study.setAttributes(ValidationTestUtils.getStudyAttributes());
        Project project = ValidationTestUtils.getProjectWithContactsAndPublications().getBaseSubmittable();
        submissionEnvelope.getStudies().add(study);
        submissionEnvelope.getProjects().add(project);
        submissionEnvelope.getProtocols().addAll(ValidationTestUtils.generateUSIProtocols());
        submissionEnvelope.getAssays().addAll(Utilities.getUSIAssayListFromDisc());
        submissionEnvelope.getAssayData().addAll(Utilities.generateUSIAssayData());
        ProcessingCertificateEnvelope processingCertificateEnvelope = metaboLightsStudyProcessor.processStudy(submissionEnvelope);
        assertEquals(9, processingCertificateEnvelope.getProcessingCertificates().size());
    }

    @Test
    public void testSampleMap() {
        USISampleToMLSample mlSample = new USISampleToMLSample();
        Sample convert = mlSample.convert(Utilities.getUSISampleFromDisc());
        SampleMap sampleMap = new SampleMap(convert);
        System.out.println(sampleMap.toString());
        assertTrue(sampleMap != null);
    }
}