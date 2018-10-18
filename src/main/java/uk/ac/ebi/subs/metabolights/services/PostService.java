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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.converters.*;
import uk.ac.ebi.subs.metabolights.model.Sample;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);


    private USIContactsToMLContacts usiContactsToMLContacts;

    private USIPublicationToMLPublication usiPublicationToMLPublication;

    private USIProtocolToMLProtocol usiProtocolToMLProtocol;

    private USIFactorToMLFactor usiFactorToMLFactor;

    private USIDescriptorToMLDescriptor usiDescriptorToMLDescriptor;

    private USISampleToMLSample usiSampleToMLSample;


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


    public uk.ac.ebi.subs.metabolights.model.Contact add(String studyID, Contact contact) {
        uk.ac.ebi.subs.metabolights.model.Contact addedContact = null;
        if (contact == null) return addedContact;
        headers.set("user_token", this.apiKey);
        try {
            ObjectNode contactsJSON = ServiceUtils.convertToJSON(usiContactsToMLContacts.convert(contact), "contact");
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(contactsJSON, headers);
            String url = mlProperties.getUrl() + studyID + "/contacts";
            ResponseEntity<uk.ac.ebi.subs.metabolights.model.Contact> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestBody, uk.ac.ebi.subs.metabolights.model.Contact.class);
            addedContact = response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
        return addedContact;
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

    public void addSample(String studyID, uk.ac.ebi.subs.data.submittable.Sample sample) {
        //Sample addedSample = null;
        try {
            List<Sample> samples = new ArrayList<>();
            headers.set("user_token", this.apiKey);
            samples.add(usiSampleToMLSample.convert(sample));
            JSONObject json = ServiceUtils.convertToJSON(samples, "samples");
            HttpEntity<JSONObject> requestBody = new HttpEntity<>(json, headers);

            String url = mlProperties.getUrl() + studyID + "/samples";
            //addedSample = restTemplate.postForObject(url, requestBody, ObjectNode.class);

            JsonNode result = restTemplate.postForObject(url, requestBody, ObjectNode.class);
            String warnings = result.path("warnings").asText();
        } catch (JSONException e) {
            logger.error(e.getMessage());
         } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void addContacts(String studyID, List<Contact> contacts) {
        for (Contact contact : contacts) {
            add(studyID, contact);
        }
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
