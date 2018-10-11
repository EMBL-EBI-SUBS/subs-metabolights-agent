package uk.ac.ebi.subs.metabolights.services;


import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONObject;
import org.omg.CORBA.Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.metabolights.converters.*;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateService {

    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    private RestTemplate restTemplate;

    private MLProperties mlProperties;

    @Value("${metabolights.apiKey}")
    private String apiKey;

    private USIContactsToMLContacts usiContactsToMLContacts;
    private USIPublicationToMLPublication usiPublicationToMLPublication;
    private USIProtocolToMLProtocol usiProtocolToMLProtocol;
    private USIFactorToMLFactor usiFactorToMLFactor;
    private USIDescriptorToMLDescriptor usiDescriptorToMLDescriptor;
    private USISampleToMLSample usiSampleToMLSample;


    public UpdateService() {
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
   }

    public void updateContact(String studyID, Contact contact) {
        if (contact == null) return;
        if (contact.getEmail() == null) return;

        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiContactsToMLContacts.convert(contact), "contact");
            HttpHeaders headers = new HttpHeaders();
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
            String url = mlProperties.getUrl() + studyID + "/contacts?email=" + contact.getEmail();
            restTemplate.put(url, requestBody, new Object[]{});

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }

    }

    public void updatePublication(String studyID, uk.ac.ebi.subs.data.component.Publication publication) {
        if (publication == null) return;
        if (publication.getArticleTitle() == null) return;

        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiPublicationToMLPublication.convert(publication), "publication");
            HttpHeaders headers = new HttpHeaders();
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
            String url = mlProperties.getUrl() + studyID + "/publications?title=" + publication.getArticleTitle();
            restTemplate.put(url, requestBody, new Object[]{});

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }

    }

    public void updateProtocol(String studyID, Protocol protocol) {
        if (protocol == null) return;
        if (protocol.getTitle() == null) return;

        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiProtocolToMLProtocol.convert(protocol), "protocol");
            HttpHeaders headers = new HttpHeaders();
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
            String url = mlProperties.getUrl() + studyID + "/protocols?name=" + protocol.getTitle();
            restTemplate.put(url, requestBody, new Object[]{});

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void updateFactor(String studyID, Attribute attribute) {
        if (attribute == null) return;
        if (attribute.getValue() == null) return;

        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiFactorToMLFactor.convert(attribute), "factor");
            HttpHeaders headers = new HttpHeaders();
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
            String url = mlProperties.getUrl() + studyID + "/factors?name=" + attribute.getValue();
            restTemplate.put(url, requestBody, new Object[]{});

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void updateDescriptor(String studyID, Attribute attribute) {
        if (attribute == null) return;
        if (attribute.getValue() == null) return;

        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiDescriptorToMLDescriptor.convert(attribute), "studyDesignDescriptor");
            HttpHeaders headers = new HttpHeaders();
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
            String url = mlProperties.getUrl() + studyID + "/descriptors?term=" + attribute.getValue();
            restTemplate.put(url, requestBody, new Object[]{});

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void updateTitle(String studyID, String title) {
        String url = mlProperties.getUrl() + studyID + "/title";
        ObjectNode titleJson = ServiceUtils.convertToJSON(title, "title");
        update(url, titleJson);
    }

    public void updateDescription(String studyID, String description) {
        String url = mlProperties.getUrl() + studyID + "/description";
        ObjectNode descriptionJson = ServiceUtils.convertToJSON(description, "description");
        update(url, descriptionJson);
    }

    public void update(String url, ObjectNode content) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(content, headers);
            restTemplate.put(url, requestBody, new Object[]{});

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void updateSample(String studyID, Sample sample) {
        String url = mlProperties.getUrl() + studyID + "/samples?name=" + sample.getAlias();
        update(sample,url);
    }

    private void update(Sample sample, String url){
        try {

            List<uk.ac.ebi.subs.metabolights.model.Sample> samples = new ArrayList<>();
            samples.add(usiSampleToMLSample.convert(sample));
            JSONObject json = ServiceUtils.convertToJSON(samples, "samples");
            HttpHeaders headers = new HttpHeaders();
            headers.set("user_token", this.apiKey);

            HttpEntity<JSONObject> requestBody = new HttpEntity<>(json, headers);
            restTemplate.put(url, requestBody, new Object[]{});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void markForDeletion(String studyID, Sample sample) {
        String newName = "__TO_BE_DELETED__" + sample.getAlias();
        String url = mlProperties.getUrl() + studyID + "/samples?name=" + sample.getAlias();
        sample.setAlias(newName);
        update(sample,url);
    }
}
