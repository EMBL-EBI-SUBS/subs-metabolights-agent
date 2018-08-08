package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.ac.ebi.subs.metabolights.model.Project;
import uk.ac.ebi.subs.metabolights.model.Study;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SubmissionToProjectTest {
    @Test
    public void generateJson() {
        USISubmissionToMLProject usiSubmissionToMLProject = new USISubmissionToMLProject();
        SubmissionEnvelope submissionEnvelope = null;
        try {
            submissionEnvelope = Utilities.getUSISubmisisonFromDisc();
            Project project = usiSubmissionToMLProject.convert(submissionEnvelope);
           
            assertEquals(project.getStudies().get(0).getSamples().size(), 16);
            assertEquals(project.getStudies().get(0).getProtocols().size(), 6);
            assertEquals(project.getStudies().get(0).getIdentifier(), "MTBLS2");
            assertEquals(project.getPeople().size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
