package uk.ac.ebi.subs.metabolights.validator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;

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
        List<Protocol> protocols =  ValidationTestUtils.generateProtocols();
        List<SingleValidationResult> validationResults = this.protocolValidator.validateContent(protocols);
        assertEquals(validationResults.size(),1);
        assertEquals(validationResults.get(0).getMessage(),
                "Protocol Sample collection has no description provided");

        protocols.get(0).setDescription("ab");
        validationResults = this.protocolValidator.validateContent(protocols);
        assertEquals(validationResults.size(),1);
        assertEquals(validationResults.get(0).getMessage(),
                "Protocol Sample collection description is not sufficient");

        protocols.get(0).setDescription("Sufficient");
        validationResults = this.protocolValidator.validateContent(protocols);
        assertEquals(validationResults.size(),0);
    }
}