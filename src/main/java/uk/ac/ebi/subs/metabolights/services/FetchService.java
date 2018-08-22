package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.metabolights.model.Investigation;
import uk.ac.ebi.subs.metabolights.model.Project;
import uk.ac.ebi.subs.metabolights.model.Study;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
public class FetchService {
    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    private static final String METABOLIGHTS_API = "http://ves-ebi-90:5000/metabolights/ws/studies/";

    private RestTemplate restTemplate;

    @Autowired
    private MLProperties mlProperties;


    public FetchService() {
        this.restTemplate = new RestTemplate();
        List messageConverters = this.restTemplate.getMessageConverters();
        messageConverters.add(new JsonAsTextPlainHttpMessageConverter());
        this.restTemplate.setMessageConverters(messageConverters);
    }

    public Study getStudy(String accession) {

        try {
            String localUrl = METABOLIGHTS_API + accession;
            Investigation investigation = restTemplate.getForObject(localUrl, Investigation.class);
            Project project = investigation.getIsaInvestigation();

            if (project != null) {
                return project.getStudies() != null && project.getStudies().size() > 0 ? project.getStudies().get(0) : null;
            }

        } catch (RestClientException e) {
            throw new RestClientException(e.getMessage(), e);
        }
        return null;
    }

    public String createNewStudyAndGetAccession() {
        String accession;
        try {
            String endPoint = METABOLIGHTS_API + "create_study";

            HttpHeaders headers = new HttpHeaders();
            headers.set("user_token", mlProperties.getApiKey());

            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            JsonNode result = restTemplate.postForObject(endPoint, entity, ObjectNode.class);
            accession = result.path("new_study").asText();
            return accession;

        } catch (RestClientException e) {
            throw new RestClientException(e.getMessage(), e);
        }
    }

}
