package uk.ac.ebi.subs.metabolights.messaging;
/**
 * This class defines the validation routing keys for the MetaboLights validator.
 */
public class MetaboLightsValidationRoutingKeys {
    public static final String EVENT_METABOLIGHTS_SAMPLE_VALIDATION = "metabolights.sample.validation";

    public static final String EVENT_METABOLIGHTS_STUDY_VALIDATION = "metabolights.study.validation";

    public static final String EVENT_METABOLIGHTS_ASSAY_VALIDATION = "metabolights.assay.validation";

    public static final String EVENT_METABOLIGHTS_ASSAYDATA_VALIDATION = "metabolights.assaydata.validation";

    public static final String EVENT_VALIDATION_SUCCESS = "validation.success";
    
    public static final String EVENT_VALIDATION_ERROR = "validation.error";
}
