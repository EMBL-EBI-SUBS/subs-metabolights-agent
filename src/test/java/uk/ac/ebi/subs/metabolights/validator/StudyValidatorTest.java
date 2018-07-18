package uk.ac.ebi.subs.metabolights.validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

import java.util.List;
import static org.junit.Assert.assertEquals;


public class StudyValidatorTest {

    private StudyValidator studyValidator;

    @Before
    public void setUp() throws Exception {
        this.studyValidator = new StudyValidator();
    }

    @After
    public void tearDown() throws Exception {
        this.studyValidator = null;
    }

    @Test
    public void validateContacts() {
        Project project = ValidationTestUtils.getProjectWithContacts();
        List<SingleValidationResult> validationResults = this.studyValidator.validateContacts(project);
        assertEquals(validationResults.size(),1);
        assertEquals(validationResults.get(0).getMessage(),
                "Contact: Alex Ben has no associated email");
    }
}