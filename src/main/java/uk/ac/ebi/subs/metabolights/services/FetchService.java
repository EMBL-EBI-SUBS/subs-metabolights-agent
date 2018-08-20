package uk.ac.ebi.subs.metabolights.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.metabolights.model.Investigation;
import uk.ac.ebi.subs.metabolights.model.Project;
import uk.ac.ebi.subs.metabolights.model.Study;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
@ConfigurationProperties(prefix = "metabolights.api")
public class FetchService {
    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

   private static final String METABOLIGHTS_API = "http://ves-ebi-8d:5000/mtbls/ws/";

//    @Value("${metabolightsUrl}")
//    String METABOLIGHTS_API;
//
//    @Value("${metabolights.api.key}")
//    String METABOLIGHTS_API_key;

    private String url;

    private RestTemplate restTemplate;

    private MLProperties mlProperties;

    public FetchService(){
        this.restTemplate = new RestTemplate();
        this.mlProperties = new MLProperties();
        List messageConverters = this.restTemplate.getMessageConverters();
        messageConverters.add(new JsonAsTextPlainHttpMessageConverter());
        this.restTemplate.setMessageConverters(messageConverters);
    }

    public Study getStudy(String accession) {

        try {
            String localUrl = METABOLIGHTS_API + "studies/" + accession;
            String loadedurl = mlProperties.getUrl();
            String status = mlProperties.getStatus();
            Investigation investigation = restTemplate.getForObject(localUrl, Investigation.class);
            Project project = investigation.getIsaInvestigation();
       
            if (project != null) {
                return project.getStudies() != null && project.getStudies().size() > 0 ? project.getStudies().get(0) : null;
            }

        } catch (RestClientException e) {
            throw new RestClientException( e.getMessage(), e);
        }
        return null;
    }

}
