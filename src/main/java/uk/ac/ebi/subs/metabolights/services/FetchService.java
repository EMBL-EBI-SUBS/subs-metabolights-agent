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
import uk.ac.ebi.subs.metabolights.model.*;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            String endPoint = mlProperties.getUrl() + "create";
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

    public void cloneNMRTemplates(String toStudy) {
        String fromStudy = "MTBLS122";
        cloneTemplate(fromStudy, toStudy);
    }

    public void cloneLcmsTemplates(String toStudy) {
        String fromStudy = "MTBLS121";
        cloneTemplate(fromStudy, toStudy);
    }

    public void cloneGcmsTemplates(String toStudy) {
        String fromStudy = "MTBLS130";
        cloneTemplate(fromStudy, toStudy);
    }

    public void cloneTemplate(String fromStudy, String toStudy) {
        String endpoint = mlProperties.getUrl() + "clone?study_id=" + fromStudy + "&to_study_id=" + toStudy;
        try {
            restTemplate.postForObject(endpoint, getHttpEntity(), ObjectNode.class);
        } catch (RestClientException e) {
            logger.error(e.getMessage());
            throw new RestClientException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    public String getMLStudyID(String biostudiesAccession) {
        String endpoint = mlProperties.getUrl() + "biostudies?biostudies_acc=" + biostudiesAccession;
        String accession = "";
        try {
            ResponseEntity<ObjectNode> response = restTemplate.exchange(
                    endpoint, HttpMethod.GET, getHttpEntity(), ObjectNode.class);
            ObjectNode result = response.getBody();
            accession = result.path("BioStudies").asText();
        } catch (Exception e) {
            return null;
        }
        return accession;
    }

    public StudyFiles getStudyFiles(String accession) {
        try {
            String localUrl = mlProperties.getUrl() + accession + "/files";

            ResponseEntity<StudyFiles> response = restTemplate.exchange(
                    localUrl, HttpMethod.GET, getHttpEntity(), StudyFiles.class);
            StudyFiles studyFiles = response.getBody();
            return studyFiles;

        } catch (Exception e) {
            logger.error(e.getMessage());
            return new StudyFiles();
        }
    }

    public MetaboLightsTable getMetaboLightsDataTable(String accession, String fileName) {
        try {
            String localUrl = mlProperties.getUrl() + accession + "/" + fileName;

            ResponseEntity<MetaboLightsTable> response = restTemplate.exchange(
                    localUrl, HttpMethod.GET, getHttpEntity(), MetaboLightsTable.class);
            return response.getBody();

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public MetaboLightsTableResult getMetaboLightsSampleDataTable(String accession, String fileName) {
        try {
            String localUrl = mlProperties.getUrl() + accession + "/" + fileName;

            ResponseEntity<MetaboLightsTableResult> response = restTemplate.exchange(
                    localUrl, HttpMethod.GET, getHttpEntity(), MetaboLightsTableResult.class);
            return response.getBody();

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private HttpEntity<String> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("user_token", this.apiKey);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return entity;
    }

    public Map<String, MetaboLightsTable> getAssayAccessionAndFileNames(String accession, List<String> assayFileNames) {
        Map<String, MetaboLightsTable> alias_filename_map = new HashMap<>();
        for (String assayFile : assayFileNames) {
            try {
                String localUrl = mlProperties.getUrl() + accession + "/" + assayFile;

                ResponseEntity<MetaboLightsTable> response = restTemplate.exchange(
                        localUrl, HttpMethod.GET, getHttpEntity(), MetaboLightsTable.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    MetaboLightsTable table = response.getBody();
                    alias_filename_map.put(assayFile, table);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                continue;
            }
        }
        return alias_filename_map;
    }

}
