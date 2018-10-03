package uk.ac.ebi.subs.metabolights.validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.metabolights.converters.Utilities;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.StudyValidationMessageEnvelope;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class StudyValidatorTest {

    private StudyValidator studyValidator;
    private StudyValidationMessageEnvelope studyValidationMessageEnvelope;

    @Before
    public void setUp() throws Exception {
        this.studyValidator = new StudyValidator();
        this.studyValidationMessageEnvelope = new StudyValidationMessageEnvelope();
        studyValidationMessageEnvelope.setEntityToValidate(Utilities.getSimpleUSIStudyFromDisc());
        studyValidationMessageEnvelope.setProtocols(ValidationTestUtils.getProtocols());
        studyValidationMessageEnvelope.setProject(ValidationTestUtils.getProjectWithContactsAndPublications());
    }

    @After
    public void tearDown() throws Exception {
        this.studyValidator = null;
    }

    @Test
    public void validateContacts() {
        Project project = ValidationTestUtils.getProjectWithContacts();
        List<SingleValidationResult> validationResults = this.studyValidator.validateContacts(project);
        assertEquals(validationResults.size(), 1);
        assertEquals(validationResults.get(0).getMessage(),
                "Contact: Alex Ben has no associated email");
    }

    @Test
    public void validatePublications() {
        Project project = ValidationTestUtils.getProjectWithPublications();
        List<SingleValidationResult> validationResults = this.studyValidator.validatePublications(project);
        assertEquals(validationResults.size(), 0);

        project.getPublications().add(ValidationTestUtils.generatePublication());
        validationResults = this.studyValidator.validatePublications(project);
        assertEquals(validationResults.size(), 1);
        assertEquals(validationResults.get(0).getMessage(), "Publication -  This is a metabolomics test study - has no associated PubMed ID or DOI");
    }

    @Test
    public void validate() {
        List<SingleValidationResult> validate = this.studyValidator.validate(studyValidationMessageEnvelope);
        assertEquals(validate.size(), 4);

    }
}