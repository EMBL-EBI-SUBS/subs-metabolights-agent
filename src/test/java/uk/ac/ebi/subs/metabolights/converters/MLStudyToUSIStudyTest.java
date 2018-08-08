package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.ac.ebi.subs.metabolights.model.Contact;
import uk.ac.ebi.subs.metabolights.model.Study;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by kalai on 19/12/2017.
 */
public class MLStudyToUSIStudyTest {
    @Test
    public void convert() throws Exception {
        Study mlStudyFromDisc = Utilities.getMLStudyFromDisc();
        MLStudyToUSIStudy mlStudyToUSIStudy = new MLStudyToUSIStudy();
        uk.ac.ebi.subs.data.submittable.Study usiStudy = mlStudyToUSIStudy.convert(mlStudyFromDisc);
        assertEquals(usiStudy.getAttributes().size(), 2);
    }
}