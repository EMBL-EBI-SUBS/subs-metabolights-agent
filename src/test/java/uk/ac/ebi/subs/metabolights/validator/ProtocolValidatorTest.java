package uk.ac.ebi.subs.metabolights.validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.model.StudyDataType;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.model.Submittable;
import java.util.List;

import static org.junit.Assert.*;

public class ProtocolValidatorTest {

    private ProtocolValidator protocolValidator;

    @Before
    public void setUp() throws Exception {
        this.protocolValidator = new ProtocolValidator();
    }

    @After
    public void tearDown() throws Exception {
        this.protocolValidator = null;
    }

    @Test
    public void validateContent() {
        List<Submittable<Protocol>> protocols = ValidationTestUtils.generateProtocols();
        List<SingleValidationResult> validationResults = this.protocolValidator.validateContent(protocols);
        assertEquals(validationResults.size(), 1);
        assertEquals(validationResults.get(0).getMessage(),
                "Protocol Sample collection has no description provided");

        protocols.get(0).setDescription("ab");
        validationResults = this.protocolValidator.validateContent(protocols);
        assertEquals(validationResults.size(), 1);
        assertEquals(validationResults.get(0).getMessage(),
                "Protocol Sample collection description is not sufficient");

        protocols.get(0).setDescription("Sufficient");
        validationResults = this.protocolValidator.validateContent(protocols);
        assertEquals(validationResults.size(), 0);
    }

    @Test
    public void validateRequiredFields() {
        validateMSImagingFields();
        validateMSFields();
        validateNmrFields();
        validateNmrImagingFields();
    }

    private void validateMSImagingFields() {
        List<Submittable<Protocol>> msImagingProtocols = ValidationTestUtils.generateProtocolsForImagingMS();
        List<SingleValidationResult> validationResults = protocolValidator.validateRequiredFields(msImagingProtocols, StudyDataType.Metabolomics_LCMS);
        assertEquals(validationResults.size(), 0);
    }

    private void validateMSFields() {
        List<Submittable<Protocol>> msProtocols = ValidationTestUtils.generateProtocolsForMS();
        List<SingleValidationResult> validationResults = protocolValidator.validateRequiredFields(msProtocols, StudyDataType.Metabolomics_LCMS);
        assertEquals(validationResults.size(), 0);

        msProtocols.remove(0);
        validationResults = protocolValidator.validateRequiredFields(msProtocols, StudyDataType.Metabolomics_LCMS);
        assertEquals(validationResults.size(), 1);
        assertEquals(validationResults.get(0).getMessage(), "Extraction protocol is not present in the study protocols");
    }

    private void validateNmrFields() {
        List<Submittable<Protocol>> nmrProtocols = ValidationTestUtils.generateProtocolsForNMR();
        List<SingleValidationResult> validationResults = protocolValidator.validateRequiredFields(nmrProtocols, StudyDataType.Metabolomics_NMR);
        assertEquals(validationResults.size(), 0);
    }

    private void validateNmrImagingFields() {
        List<Submittable<Protocol>> nmrImagingProtocols = ValidationTestUtils.generateProtocolsForImagingNMR();
        List<SingleValidationResult> validationResults = protocolValidator.validateRequiredFields(nmrImagingProtocols, StudyDataType.Metabolomics_NMR);
        assertEquals(validationResults.size(), 0);

        nmrImagingProtocols.remove(3);
        validationResults = protocolValidator.validateRequiredFields(nmrImagingProtocols, StudyDataType.Metabolomics_NMR);
        assertEquals(validationResults.size(), 3);

        nmrImagingProtocols = ValidationTestUtils.generateProtocolsForImagingNMRWithMissingEntries();
        validationResults = protocolValidator.validateRequiredFields(nmrImagingProtocols, StudyDataType.Metabolomics_NMR);
        assertEquals(validationResults.size(), 2);
    }
}