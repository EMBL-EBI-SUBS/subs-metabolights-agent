package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.ac.ebi.subs.metabolights.model.Project;
import uk.ac.ebi.subs.metabolights.model.Study;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SubmissionToStudyTest {
    @Test
    public void generateJson() {
        USISubmissionToMLStudy usiSubmissionToMLStudy = new USISubmissionToMLStudy();
        SubmissionEnvelope submissionEnvelope = null;
        try {
            submissionEnvelope = WSUtils.getUSISubmisisonFromDisc();
            List<Study> mlStudies = usiSubmissionToMLStudy.convert(submissionEnvelope);
            Project project = new Project();
            project.setStudies(mlStudies);
            project.setTitle("Investigation");
            ObjectMapper mapper = new ObjectMapper();
            String mlStudy = mapper.writeValueAsString(project);
            System.out.println(mlStudy);

            assertEquals(mlStudies.get(0).getSamples().size(), 16);
            assertEquals(mlStudies.get(0).getProtocols().size(), 6);
            assertEquals(mlStudies.get(0).getIdentifier(), "MTBLS2");
            assertEquals(mlStudies.get(0).getPeople().size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
