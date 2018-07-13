package uk.ac.ebi.subs.metabolights.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.subs.messaging.ExchangeConfig;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Queues;

import static uk.ac.ebi.subs.metabolights.messaging.MetaboLightsValidationQueues.*;
import static uk.ac.ebi.subs.metabolights.messaging.MetaboLightsValidationRoutingKeys.*;

@Configuration
@ComponentScan(basePackageClasses = ExchangeConfig.class)
public class MetaboLightsQueueConfiguration {
    /**
     * Instantiate a {@link Queue} to publish Studies for metabolights validation.
     *
     * @return an instance of a {@link Queue} to publish studies.
     */
    @Bean
    public Queue metabolightsStudyValidationQueue() {
        return buildQueueWithDlx(METABOLIGHTS_STUDY_VALIDATION);
    }

    /**
     * Create a {@link Binding} between the validation exchange and Metabolights study validation queue
     * using the metabolights study validation routing key for validation events of studies.
     *
     * @param metabolightsStudyValidationQueue to validate studies.
     * @param submissionExchange               {@link TopicExchange} for validation
     * @return a {@link Binding}  between the validation exchange and metabolights study validation queue
     * using the routing key of created studies.
     */
    @Bean
    public Binding metabolightsStudyValidationBinding(Queue metabolightsStudyValidationQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(metabolightsStudyValidationQueue).to(submissionExchange).with(EVENT_METABOLIGHTS_STUDY_VALIDATION);
    }

    /**
     * Instantiate a {@link Queue} to publish Samples for metabolights validation.
     *
     * @return an instance of a {@link Queue} to publish Samples.
     */
    @Bean
    public Queue metabolightsSampleValidationQueue() {
        return buildQueueWithDlx(METABOLIGHTS_SAMPLE_VALIDATION);
    }

    /**
     * Create a {@link Binding} between the validation exchange and Metabolights Sample validation queue
     * using the metabolights sample validation routing key for validation events of samples.
     *
     * @param metabolightsSampleValidationQueue to validate studies.
     * @param submissionExchange               {@link TopicExchange} for validation
     * @return a {@link Binding}  between the validation exchange and metabolights sample validation queue
     * using the routing key of created samples.
     */
    @Bean
    public Binding metabolightsSampleValidationBinding(Queue metabolightsSampleValidationQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(metabolightsSampleValidationQueue).to(submissionExchange).with(EVENT_METABOLIGHTS_SAMPLE_VALIDATION);
    }


    /**
     * Instantiate a {@link Queue} to publish assays for metabolights validation.
     *
     * @return an instance of a {@link Queue} to publish assays.
     */
    @Bean
    public Queue metabolightsAssayValidationQueue() {
        return buildQueueWithDlx(METABOLIGHTS_ASSAY_VALIDATION);
    }

    /**
     * Create a {@link Binding} between the validation exchange and Metabolights assay validation queue
     * using the metabolights assay validation routing key for validation events of assays.
     *
     * @param metabolightsAssayValidationQueue to validate studies.
     * @param submissionExchange               {@link TopicExchange} for validation
     * @return a {@link Binding}  between the validation exchange and metabolights assay validation queue
     * using the routing key of created assays.
     */
    @Bean
    public Binding metabolightsAssayValidationBinding(Queue metabolightsAssayValidationQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(metabolightsAssayValidationQueue).to(submissionExchange).with(EVENT_METABOLIGHTS_ASSAY_VALIDATION);
    }


    /**
     * Instantiate a {@link Queue} to publish assayData for metabolights validation.
     *
     * @return an instance of a {@link Queue} to publish assayData.
     */
    @Bean
    public Queue metabolightsAssaydataValidationQueue() {
        return buildQueueWithDlx(METABOLIGHTS_ASSAYDATA_VALIDATION);
    }

    /**
     * Create a {@link Binding} between the validation exchange and Metabolights assayData validation queue
     * using the metabolights assayData validation routing key for validation events of assaysData.
     *
     * @param metabolightsAssaydataValidationQueue to validate studies.
     * @param submissionExchange               {@link TopicExchange} for validation
     * @return a {@link Binding}  between the validation exchange and metabolights assayData validation queue
     * using the routing key of created assayData.
     */
    @Bean
    public Binding metabolightsAssaydataValidationBinding(Queue metabolightsAssaydataValidationQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(metabolightsAssaydataValidationQueue).to(submissionExchange).with(EVENT_METABOLIGHTS_ASSAYDATA_VALIDATION);
    }

    /**
     * Build an instance of a {@link Queue} configured with a Dead Letter Exchange.
     * @param queueName
     * @return
     */
    public static Queue buildQueueWithDlx(String queueName) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", Exchanges.DEAD_LETTER_EXCHANGE)
                .build();
    }
}
