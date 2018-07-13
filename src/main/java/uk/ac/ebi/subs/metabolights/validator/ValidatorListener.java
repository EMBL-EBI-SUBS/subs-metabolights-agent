package uk.ac.ebi.subs.metabolights.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.validator.data.*;

import java.util.List;

import static uk.ac.ebi.subs.metabolights.messaging.MetaboLightsValidationQueues.*;
import static uk.ac.ebi.subs.metabolights.messaging.MetaboLightsValidationRoutingKeys.EVENT_VALIDATION_ERROR;
import static uk.ac.ebi.subs.metabolights.messaging.MetaboLightsValidationRoutingKeys.EVENT_VALIDATION_SUCCESS;

@Service
public class ValidatorListener {

    private static Logger logger = LoggerFactory.getLogger(ValidatorListener.class);
    private RabbitMessagingTemplate rabbitMessagingTemplate;
    private StudyValidator studyValidator;

    public ValidatorListener(RabbitMessagingTemplate rabbitMessagingTemplate,
                             StudyValidator studyValidator) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.studyValidator = studyValidator;
    }

    @RabbitListener(queues = METABOLIGHTS_STUDY_VALIDATION)
    public void processStudyValidationRequest(StudyValidationMessageEnvelope envelope){
        logger.info("Got study to validate with ID: {}.", envelope.getEntityToValidate().getId());

        logger.info("MetaboLights Study validation done.");
        List<SingleValidationResult> validatedResults = studyValidator.validate(envelope);
        validatedResults = ValidationUtils.getSinglePassResultIfNoErrors(validatedResults);

        sendResults(
                ValidationUtils.buildSingleValidationResultsEnvelope(validatedResults,
                        envelope.getValidationResultVersion(),
                        envelope.getValidationResultUUID()),
                ValidationUtils.hasValidationError(validatedResults)
        );
    }

    @RabbitListener(queues = METABOLIGHTS_ASSAY_VALIDATION)
    public void processAssayValidationRequest(AssayValidationMessageEnvelope envelope){
        logger.info("Got assay to validate with ID: {}.", envelope.getEntityToValidate().getId());
    }

    @RabbitListener(queues = METABOLIGHTS_SAMPLE_VALIDATION)
    public void processSampleValidationRequest(SampleValidationMessageEnvelope envelope){
        logger.info("Got sample to validate with ID: {}.", envelope.getEntityToValidate().getId());
    }

    @RabbitListener(queues = METABOLIGHTS_ASSAYDATA_VALIDATION)
    public void processAssayDataValidationRequest(AssayDataValidationMessageEnvelope envelope){
        logger.info("Got assayData to validate with ID: {}.", envelope.getEntityToValidate().getId());
    }

    private void sendResults(SingleValidationResultsEnvelope singleValidationResultsEnvelope, boolean hasValidationError) {
        if (hasValidationError) {
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_VALIDATION_ERROR, singleValidationResultsEnvelope);
        } else {
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_VALIDATION_SUCCESS, singleValidationResultsEnvelope);
        }
    }

}
