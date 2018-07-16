package uk.ac.ebi.subs.metabolights.validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Protocol;
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

    @Test
    public void validateProtocols() {
        List<Protocol> protocols =  ValidationTestUtils.generateProtocols();
        List<SingleValidationResult> validationResults = this.studyValidator.validateProtocols(protocols);
        assertEquals(validationResults.size(),1);
        assertEquals(validationResults.get(0).getMessage(),
                "Protocol Sample collection has no description provided");

        protocols.get(0).setDescription("ab");
        validationResults = this.studyValidator.validateProtocols(protocols);
        assertEquals(validationResults.size(),1);
        assertEquals(validationResults.get(0).getMessage(),
                "Protocol Sample collection description is not sufficient");

        protocols.get(0).setDescription("Sufficient");
        validationResults = this.studyValidator.validateProtocols(protocols);
        assertEquals(validationResults.size(),0);
    }
}