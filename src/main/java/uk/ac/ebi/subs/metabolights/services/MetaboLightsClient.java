package uk.ac.ebi.subs.metabolights.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
public class MetaboLightsClient {

    private static final Logger logger = LoggerFactory.getLogger(MetaboLightsClient.class);

    private RestTemplate restTemplate;

    private MLProperties mlProperties;


    public MetaboLightsClient() {
        this.restTemplate = new RestTemplate();
        List messageConverters = this.restTemplate.getMessageConverters();
        messageConverters.add(new JsonAsTextPlainHttpMessageConverter());
        this.restTemplate.setMessageConverters(messageConverters);
        mlProperties = new MLProperties();
    }

    public void add(){

    }

}
