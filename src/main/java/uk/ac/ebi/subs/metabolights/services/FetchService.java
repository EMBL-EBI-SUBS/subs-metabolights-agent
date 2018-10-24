package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import uk.ac.ebi.subs.metabolights.model.StudyFiles;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
public class FetchService {
    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    private RestTemplate restTemplate;

    private MLProperties mlProperties;

    @Value("${metabolights.apiKey}")
    private String apiKey;


    public FetchService() {
        this.restTemplate = new RestTemplate();
        List messageConverters = this.restTemplate.getMessageConverters();
        messageConverters.add(new JsonAsTextPlainHttpMessageConverter());
        this.restTemplate.setMessageConverters(messageConverters);
        mlProperties = new MLProperties();
    }

    public Study getStudy(String accession) {

        try {
            String localUrl = mlProperties.getUrl() + accession;
            ResponseEntity<Investigation> response = restTemplate.exchange(
                    localUrl, HttpMethod.GET, getHttpEntity(), Investigation.class);
            Investigation investigation = response.getBody();

            Project project = investigation.getIsaInvestigation();

            if (project != null) {
                return project.getStudies() != null && project.getStudies().size() > 0 ? project.getStudies().get(0) : null;
            }

        } catch (RestClientException e) {
            logger.error(e.getMessage());
            throw new RestClientException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
        return null;
    }

    public String createNewStudyAndGetAccession() {
        String accession;
        try {
            String endPoint = mlProperties.getUrl() + "create_study";
            JsonNode result = restTemplate.postForObject(endPoint, getHttpEntity(), ObjectNode.class);
            accession = result.path("new_study").asText();
            return accession;

        } catch (RestClientException e) {
            logger.error(e.getMessage());
            throw new RestClientException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    private HttpEntity<String> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("user_token", this.apiKey);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return entity;
    }

}
