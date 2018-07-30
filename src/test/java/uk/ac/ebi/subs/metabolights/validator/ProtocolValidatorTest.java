package uk.ac.ebi.subs.metabolights.validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.StudyDataType;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.ArrayList;
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
        List<SingleValidationResult> validationResults = new ArrayList<>();
        List<Submittable<Protocol>> msImagingProtocols =  ValidationTestUtils.generateProtocolsForImagingMS();
        validationResults = protocolValidator.validateRequiredFields(msImagingProtocols, StudyDataType.Metabolomics_LCMS);
        assertEquals(validationResults.size(),0);

        List<Submittable<Protocol>> msProtocols =  ValidationTestUtils.generateProtocolsForMS();
        validationResults = protocolValidator.validateRequiredFields(msProtocols, StudyDataType.Metabolomics_LCMS);
        assertEquals(validationResults.size(),0);

        List<Submittable<Protocol>> nmrProtocols = ValidationTestUtils.generateProtocolsForNMR();
        validationResults = protocolValidator.validateRequiredFields(nmrProtocols, StudyDataType.Metabolomics_NMR);
        assertEquals(validationResults.size(), 0);

        List<Submittable<Protocol>> nmrImagingProtocols = ValidationTestUtils.generateProtocolsForImagingNMR();
        validationResults = protocolValidator.validateRequiredFields(nmrImagingProtocols, StudyDataType.Metabolomics_NMR);
        assertEquals(validationResults.size(), 0);

        nmrImagingProtocols = ValidationTestUtils.generateProtocolsForImagingNMRWithMissingEntries();
        validationResults = protocolValidator.validateRequiredFields(nmrImagingProtocols, StudyDataType.Metabolomics_NMR);
        assertEquals(validationResults.size(), 2);

    }
}