package uk.ac.ebi.subs.metabolights.validator.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.metabolights.validator.ValidationUtils;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.LocalDateCustomSerializer;
import uk.ac.ebi.subs.metabolights.validator.schema.model.JsonSchemaValidationError;
import uk.ac.ebi.subs.validator.data.*;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Data
public class JsonSchemaValidationHandler {

    // Temporary solution - schema url should be provided not hardcoded
    @Value("${mlsample.schema.url}")
    private String mlSampleSchemaUrl;
    @Value("${mlstudy.schema.url}")
    private String mlStudySchemaUrl;
    @Value("${mlassay.schema.url}")
    private String mlAssaySchemaUrl;
//    @Value("${assaydata.schema.url}")
//    private String assayDataSchemaUrl;

    private JsonSchemaValidationService validationService;
    private SchemaService schemaService;
    private ObjectMapper mapper = new ObjectMapper();
    private SimpleModule module = new SimpleModule();

    public JsonSchemaValidationHandler(JsonSchemaValidationService validationService, SchemaService schemaService) {
        this.validationService = validationService;
        this.schemaService = schemaService;
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY); // Null fields and empty collections are not included in the serialization.
        this.module.addSerializer(LocalDate.class, new LocalDateCustomSerializer());
        this.mapper.registerModule(module);
    }

    public List<SingleValidationResult> handleSampleValidation(SampleValidationMessageEnvelope envelope) {
        JsonNode sampleSchema = schemaService.getSchemaFor(envelope.getEntityToValidate().getClass().getTypeName(), mlSampleSchemaUrl);

        List<JsonSchemaValidationError> jsonSchemaValidationErrors = validationService.validate(sampleSchema, mapper.valueToTree(envelope.getEntityToValidate()));
        List<SingleValidationResult> singleValidationResultList = getSingleValidationResults(envelope, jsonSchemaValidationErrors);
        return singleValidationResultList;
    }

    // -- Helper methods -- //
    private List<SingleValidationResult> getSingleValidationResults(ValidationMessageEnvelope envelope, List<JsonSchemaValidationError> jsonSchemaValidationErrors) {
        List<SingleValidationResult> singleValidationResultList;
        if (jsonSchemaValidationErrors.isEmpty()) {
            singleValidationResultList = Arrays.asList(ValidationUtils.generatePassingSingleValidationResult(envelope.getEntityToValidate().getId(), ValidationAuthor.JsonSchema));
        } else {
            singleValidationResultList = convertToSingleValidationResultList(jsonSchemaValidationErrors, envelope.getEntityToValidate().getId());
        }
        return singleValidationResultList;
    }

    private List<SingleValidationResult> convertToSingleValidationResultList(List<JsonSchemaValidationError> errorList, String entityUuid) {
        List<SingleValidationResult> validationResults = new ArrayList<>();
        for (JsonSchemaValidationError error : errorList) {
            validationResults.add(generateSchemaSingleValidationResult(error, entityUuid));
        }
        return validationResults;
    }

    private SingleValidationResult generateSchemaSingleValidationResult(JsonSchemaValidationError error, String entityUuid) {
        SingleValidationResult validationResult = new SingleValidationResult();
        validationResult.setValidationAuthor(ValidationAuthor.JsonSchema);
        validationResult.setValidationStatus(SingleValidationResultStatus.Error);
        validationResult.setEntityUuid(entityUuid);
        validationResult.setMessage(error.getDataPath() + " error(s): " + error.getErrorsAsString());
        return validationResult;
    }

}
