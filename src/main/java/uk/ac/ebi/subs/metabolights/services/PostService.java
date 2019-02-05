package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.converters.*;
import uk.ac.ebi.subs.metabolights.model.*;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);


    private USIContactsToMLContacts usiContactsToMLContacts;

    private USIPublicationToMLPublication usiPublicationToMLPublication;

    private USIProtocolToMLProtocol usiProtocolToMLProtocol;

    private USIFactorToMLFactor usiFactorToMLFactor;

    private USIDescriptorToMLDescriptor usiDescriptorToMLDescriptor;

    private USISampleToMLSample usiSampleToMLSample;

    private USIAssayToMLNMRAssayTable usiAssayToMLNMRAssayTable;


    private RestTemplate restTemplate;

    private MLProperties mlProperties;

    @Value("${metabolights.apiKey}")
    private String apiKey;

    private HttpHeaders headers;


    public PostService() {
        this.restTemplate = new RestTemplate();
        List messageConverters = this.restTemplate.getMessageConverters();
        messageConverters.add(new JsonAsTextPlainHttpMessageConverter());

        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        messageConverters.add(jsonHttpMessageConverter);
        this.restTemplate.setMessageConverters(messageConverters);

        usiContactsToMLContacts = new USIContactsToMLContacts();
        usiPublicationToMLPublication = new USIPublicationToMLPublication();
        usiProtocolToMLProtocol = new USIProtocolToMLProtocol();
        usiFactorToMLFactor = new USIFactorToMLFactor();
        usiDescriptorToMLDescriptor = new USIDescriptorToMLDescriptor();
        usiSampleToMLSample = new USISampleToMLSample();

        mlProperties = new MLProperties();
        headers = new HttpHeaders();
        headers.set("save_audit_copy", "false");
    }


    public List<uk.ac.ebi.subs.metabolights.model.Contact> addContacts(String studyID, List<Contact> contacts) {
        List<uk.ac.ebi.subs.metabolights.model.Contact> addedContacts = new ArrayList<>();
        if (contacts == null || contacts.isEmpty()) return addedContacts;
        headers.set("user_token", this.apiKey);
        try {
            ObjectNode contactsJSON = ServiceUtils.convertToJSON(convert(contacts), "contacts");
            System.out.println("Object node - " + contactsJSON);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(contactsJSON, headers);
            String url = mlProperties.getUrl() + studyID + "/contacts";
            ResponseEntity<Contacts> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestBody, Contacts.class);
            addedContacts = response.getBody().getContacts();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
        return addedContacts;
    }

    private List<uk.ac.ebi.subs.metabolights.model.Contact> convert(List<Contact> contacts) {
        List<uk.ac.ebi.subs.metabolights.model.Contact> mlContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            mlContacts.add(usiContactsToMLContacts.convert(contact));
        }
        return mlContacts;
    }

    public uk.ac.ebi.subs.metabolights.model.Publication add(String studyID, Publication publication) {
        uk.ac.ebi.subs.metabolights.model.Publication addedpublication = null;
        if (publication == null) return addedpublication;
        headers.set("user_token", this.apiKey);
        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiPublicationToMLPublication.convert(publication), "publication");
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);

            String url = mlProperties.getUrl() + studyID + "/publications";
            ResponseEntity<uk.ac.ebi.subs.metabolights.model.Publication> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestBody, uk.ac.ebi.subs.metabolights.model.Publication.class);
            addedpublication = response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
        return addedpublication;
    }

    public uk.ac.ebi.subs.metabolights.model.Protocol add(String studyID, uk.ac.ebi.subs.data.submittable.Protocol protocol) {
        uk.ac.ebi.subs.metabolights.model.Protocol addedProtocol = null;
        if (protocol == null) return addedProtocol;
        headers.set("user_token", this.apiKey);
        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiProtocolToMLProtocol.convert(protocol), "protocol");
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);

            String url = mlProperties.getUrl() + studyID + "/protocols";
            ResponseEntity<uk.ac.ebi.subs.metabolights.model.Protocol> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestBody, uk.ac.ebi.subs.metabolights.model.Protocol.class);
            addedProtocol = response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
        return addedProtocol;
    }

    public uk.ac.ebi.subs.metabolights.model.Factor addFactor(String studyID, Attribute attribute) {
        uk.ac.ebi.subs.metabolights.model.Factor addedFactor = null;
        if (attribute == null) return addedFactor;
        headers.set("user_token", this.apiKey);
        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiFactorToMLFactor.convert(attribute), "factor");
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);

            String url = mlProperties.getUrl() + studyID + "/factors";
            ResponseEntity<uk.ac.ebi.subs.metabolights.model.Factor> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestBody, uk.ac.ebi.subs.metabolights.model.Factor.class);
            addedFactor = response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
        return addedFactor;
    }

    public uk.ac.ebi.subs.metabolights.model.OntologyModel addDescriptor(String studyID, Attribute attribute) {
        uk.ac.ebi.subs.metabolights.model.OntologyModel addedDescriptor = null;
        if (attribute == null) return addedDescriptor;
        headers.set("user_token", this.apiKey);
        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiDescriptorToMLDescriptor.convert(attribute), "studyDesignDescriptor");
            System.out.println("JSON = " + json);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);

            String url = mlProperties.getUrl() + studyID + "/descriptors";
            ResponseEntity<uk.ac.ebi.subs.metabolights.model.OntologyModel> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestBody, uk.ac.ebi.subs.metabolights.model.OntologyModel.class);
            addedDescriptor = response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
        return addedDescriptor;
    }

    public void addSamples(List<uk.ac.ebi.subs.data.submittable.Sample> samples, String studyID, String sampleFileName, Map<String, Header> existingSampleTableHeaders) {
        if (samples == null || samples.size() == 0) {
            return;
        }
        try {
            SampleRows sampleRows = new SampleRows();
            for (uk.ac.ebi.subs.data.submittable.Sample sample : samples) {
                SampleMap sampleMap = new SampleMap(usiSampleToMLSample.convert(sample));
                if (existingSampleTableHeaders != null) {
                    ServiceUtils.fillEmptyValuesForMissingColumns(sampleMap, existingSampleTableHeaders);
                }
                sampleRows.add(sampleMap);
            }
            ObjectNode objectNode = ServiceUtils.convertToJSON(sampleRows, "data");
            System.out.println("Sample rows to save: " + objectNode);
            addRows(studyID, objectNode, sampleFileName);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSampleRows(List<uk.ac.ebi.subs.data.submittable.Sample> samples, String studyID, String sampleFileName, Map<String, String> existingSampleTableHeaders) {
        if (samples == null || samples.size() == 0) {
            return;
        }
        try {
            SampleRows sampleRows = new SampleRows();
            for (uk.ac.ebi.subs.data.submittable.Sample sample : samples) {
                SampleMap sampleMap = new SampleMap(usiSampleToMLSample.convert(sample));
                if (existingSampleTableHeaders != null) {
                    ServiceUtils.fillEmptyValuesForMissingColumnsForSamples(sampleMap, existingSampleTableHeaders);
                }
                sampleRows.add(sampleMap);
            }
            //todo get sample columns that are not present in existingSampleTableHeaders
            //todo add column, but how to keep track of column indexes for TSF and TAN?
            //todo sampleMap is linkedlist and will return the elements in the order of insertion so use this to insert TSF and TAN, Get last index from headers
            //todo start index to add, from the size of the existingSampleTableHeaders
            //todo check only one sampleMap from the SampleRow to add the additional column. 


            ObjectNode objectNode = ServiceUtils.convertToJSON(sampleRows, "data");
            System.out.println("Sample rows to save: " + objectNode);
            addRows(studyID, objectNode, sampleFileName);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpStatus addNewAssay(NewMetabolightsAssay newMetabolightsAssay, String studyID) {
        ObjectNode json = ServiceUtils.convertToJSON(newMetabolightsAssay, "assay");
        String url = mlProperties.getUrl() + studyID + "/assays";
        headers.set("user_token", this.apiKey);
        HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
        ResponseEntity<NewAssayResult> exchange = restTemplate.exchange(
                url, HttpMethod.POST, requestBody, NewAssayResult.class);
        return exchange.getStatusCode();
    }

    public void addAssayRows(List<uk.ac.ebi.subs.data.submittable.Assay> assays, String studyID, String assayFileName, Map<String, Header> existingAssayTableHeaders) {
        if (assays == null || assays.size() == 0) {
            return;
        }
        try {
            AssayRows assayRows = new AssayRows();
            for (uk.ac.ebi.subs.data.submittable.Assay assay : assays) {
                NMRAssayMap assayMap = new NMRAssayMap(assay);
                if (existingAssayTableHeaders != null) {
                    ServiceUtils.fillEmptyValuesForMissingColumns(assayMap, existingAssayTableHeaders);
                }
                assayRows.add(assayMap);
            }
            ObjectNode objectNode = ServiceUtils.convertToJSON(assayRows, "data");
            System.out.println("Assay rows to save: " + objectNode);
            addRows(studyID, objectNode, assayFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpStatus addRows(String studyID, ObjectNode json, String fileName) throws Exception {
        System.out.println("json to update = " + json);
        headers.set("user_token", this.apiKey);
        // headers.set("Content-type", "application/json; charset=utf-8");
        String url = mlProperties.getUrl() + studyID + "/rows/" + fileName;
        HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
        ResponseEntity<MetaboLightsTableResult> exchange = restTemplate.exchange(
                url, HttpMethod.POST, requestBody, MetaboLightsTableResult.class);
        return exchange.getStatusCode();
    }

    public void addPublications(String studyID, List<Publication> publications) {
        for (Publication publication : publications) {
            add(studyID, publication);
        }
    }

    public void addStudyDesignDescriptors(String studyID, List<Attribute> descriptors) {
        for (Attribute attribute : descriptors) {
            addDescriptor(studyID, attribute);
        }
    }

    public void addStudyFactors(String studyID, List<Attribute> factors) {
        for (Attribute attribute : factors) {
            addFactor(studyID, attribute);
        }
    }

    public void addStudyProtocols(String studyID, List<Protocol> protocols) {
        for (Protocol protocol : protocols) {
            add(studyID, protocol);
        }
    }
}
