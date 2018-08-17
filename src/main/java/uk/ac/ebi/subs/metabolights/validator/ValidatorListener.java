package uk.ac.ebi.subs.metabolights.validator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;

import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.validator.data.*;

import java.util.List;

import static uk.ac.ebi.subs.metabolights.messaging.MetaboLightsValidationQueues.*;
import static uk.ac.ebi.subs.metabolights.messaging.MetaboLightsValidationRoutingKeys.EVENT_VALIDATION_ERROR;
import static uk.ac.ebi.subs.metabolights.messaging.MetaboLightsValidationRoutingKeys.EVENT_VALIDATION_SUCCESS;

@Service
@RequiredArgsConstructor
public class ValidatorListener {

    private static Logger logger = LoggerFactory.getLogger(ValidatorListener.class);
    @NonNull
    private RabbitMessagingTemplate rabbitMessagingTemplate;
    @NonNull
    private StudyValidator studyValidator;
    @NonNull
    private AssayValidator assayValidator;
    @NonNull
    private AssayDataValidator assayDataValidator;
    @NonNull
    private SampleValidator sampleValidator;

    @RabbitListener(queues = METABOLIGHTS_STUDY_VALIDATION)
    public void processStudyValidationRequest(StudyValidationMessageEnvelope envelope) {
        logger.info("Got study to validate with ID: {}.", envelope.getEntityToValidate().getId());
        List<SingleValidationResult> validatedResults = studyValidator.validate(envelope);
        processAndSendResults(envelope, validatedResults);
        logger.info("MetaboLights Study validation done.");
    }

    @RabbitListener(queues = METABOLIGHTS_ASSAY_VALIDATION)
    public void processAssayValidationRequest(AssayValidationMessageEnvelope envelope) {
        logger.info("Got assay to validate with ID: {}.", envelope.getEntityToValidate().getId());
        List<SingleValidationResult> validatedResults = assayValidator.validate(envelope);
        processAndSendResults(envelope, validatedResults);
        logger.info("MetaboLights Assay validation done.");
    }

    @RabbitListener(queues = METABOLIGHTS_SAMPLE_VALIDATION)
    public void processSampleValidationRequest(SampleValidationMessageEnvelope envelope) {
        logger.info("Got sample to validate with ID: {}.", envelope.getEntityToValidate().getId());
        List<SingleValidationResult> validatedResults = sampleValidator.validate(envelope);
        processAndSendResults(envelope, validatedResults);
    }

    @RabbitListener(queues = METABOLIGHTS_ASSAYDATA_VALIDATION)
    public void processAssayDataValidationRequest(AssayDataValidationMessageEnvelope envelope) {
        logger.info("Got assayData to validate with ID: {}.", envelope.getEntityToValidate().getId());
        List<SingleValidationResult> validatedResults = assayDataValidator.validate(envelope);
        processAndSendResults(envelope, validatedResults);
        logger.info("MetaboLights Assay validation done.");
    }

    private void processAndSendResults(ValidationMessageEnvelope validationMessageEnvelope, List<SingleValidationResult> validatedResults) {
        validatedResults = ValidationUtils.getSinglePassResultIfNoErrors(validatedResults);
        sendResults(
                ValidationUtils.buildSingleValidationResultsEnvelope(validatedResults,
                        validationMessageEnvelope.getValidationResultVersion(),
                        validationMessageEnvelope.getValidationResultUUID()),
                ValidationUtils.hasValidationError(validatedResults)
        );
    }

    private void sendResults(SingleValidationResultsEnvelope singleValidationResultsEnvelope, boolean hasValidationError) {
        if (hasValidationError) {
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_VALIDATION_ERROR, singleValidationResultsEnvelope);
        } else {
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_VALIDATION_SUCCESS, singleValidationResultsEnvelope);
        }
    }

}
