package uk.ac.ebi.subs.metabolights.validator.schema;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.metabolights.TestUtils.MesssageEnvelopeTestHelper;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;


import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class JsonSchemaValidationHandlerTest {
    JsonSchemaValidationHandler jsonSchemaValidationHandler;
    JsonSchemaValidationService jsonSchemaValidationService;

    private RestTemplate restTemplate = new RestTemplate();
    private SchemaService schemaService;

    @Before
    public void setUp() {
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            protected boolean hasError(HttpStatus statusCode) {
                return false;
            }
        });

        schemaService = new SchemaService(restTemplate);

        jsonSchemaValidationService = new JsonSchemaValidationService(restTemplate);
        jsonSchemaValidationService.setJsonSchemaValidator("https://subs-json-schema-validator.herokuapp.com/validate");

        jsonSchemaValidationHandler = new JsonSchemaValidationHandler(jsonSchemaValidationService, schemaService);
        jsonSchemaValidationHandler.setMlSampleSchemaUrl("https://raw.githubusercontent.com/EMBL-EBI-SUBS/validation-schemas/master/sample/ml-sample-schema.json");
    }

    @Test
    public void handleSampleValidation() {
        SampleValidationMessageEnvelope sampleValidationEnvelope = MesssageEnvelopeTestHelper.getSampleValidationEnvelope();
        List<SingleValidationResult> validationResults = jsonSchemaValidationHandler.handleSampleValidation(sampleValidationEnvelope);

        assertEquals(validationResults.size(), 2);
        assertEquals(validationResults.get(0).getMessage(), ".attributes.Organism error(s): should have required property 'Organism'.");
        assertEquals(validationResults.get(1).getMessage(), ".attributes.Organism part error(s): should have required property 'Organism part'.");
    }
}