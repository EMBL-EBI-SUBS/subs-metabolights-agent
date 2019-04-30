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
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.metabolights.converters.*;
import uk.ac.ebi.subs.metabolights.model.*;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private HttpHeaders headers;


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
        headers = new HttpHeaders();
        headers.set("save_audit_copy", "false");
    }

    public void updateContact(String studyID, Contact contact) {
        if (contact == null) return;
        if (contact.getEmail() == null) return;

        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiContactsToMLContacts.convert(contact), "contact");
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
            String url = mlProperties.getUrl() + studyID + "/contacts?email=" + contact.getEmail();
            // restTemplate.put(url, requestBody, new Object[]{});
            restTemplate.exchange(
                    url, HttpMethod.PUT, requestBody, uk.ac.ebi.subs.metabolights.model.Contact.class);

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
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
            String url = mlProperties.getUrl() + studyID + "/publications?title=" + publication.getArticleTitle();
            // restTemplate.put(url, requestBody, new Object[]{});
            restTemplate.exchange(
                    url, HttpMethod.PUT, requestBody, uk.ac.ebi.subs.metabolights.model.Publication.class);

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
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
            String url = mlProperties.getUrl() + studyID + "/protocols?name=" + protocol.getTitle();
            // restTemplate.put(url, requestBody, new Object[]{});
            restTemplate.exchange(
                    url, HttpMethod.PUT, requestBody, uk.ac.ebi.subs.metabolights.model.Protocol.class);

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
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
            String url = mlProperties.getUrl() + studyID + "/factors?name=" + attribute.getValue();
            //  restTemplate.put(url, requestBody, new Object[]{});
            restTemplate.exchange(
                    url, HttpMethod.PUT, requestBody, uk.ac.ebi.subs.metabolights.model.Factor.class);

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
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
            String url = mlProperties.getUrl() + studyID + "/descriptors?term=" + attribute.getValue();
//            restTemplate.put(url, requestBody, new Object[]{});
            restTemplate.exchange(
                    url, HttpMethod.PUT, requestBody, uk.ac.ebi.subs.metabolights.model.OntologyModel.class);

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
            headers.set("user_token", this.apiKey);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(content, headers);
            restTemplate.put(url, requestBody, new Object[]{});
//            restTemplate.exchange(
//                    url, HttpMethod.PUT, requestBody, java.lang.Object.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void updateSamples(List<Sample> samples, String studyID, String sampleFileName, Map<String, String> existingSampleTableHeaders) {
        if (samples == null || samples.size() == 0) {
            return;
        }
        try {
            SampleRows sampleRows = new SampleRows();
            for (Sample sample : samples) {
                SampleMap sampleMap = new SampleMap(usiSampleToMLSample.convert(sample));
                ServiceUtils.fillEmptyValuesForMissingColumnsForSamples(sampleMap, existingSampleTableHeaders);
                sampleRows.add(sampleMap);
            }

            ObjectNode json = ServiceUtils.convertToJSON(sampleRows, "data");
            updateRows(studyID, json, sampleFileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateAssays(List<uk.ac.ebi.subs.data.submittable.Assay> assays, String studyID, String assayFileName, Map<String, Header> existingAssayTableHeaders) {
        if (assays == null || assays.size() == 0) {
            return;
        }
        try {
            AssayRows assayRows = new AssayRows();
            for (Assay assay : assays) {
                NMRAssayMap assayMap = new NMRAssayMap(assay);
                if (existingAssayTableHeaders != null) {
                    ServiceUtils.fillEmptyValuesForMissingColumns(assayMap, existingAssayTableHeaders);
                }
                assayRows.add(assayMap);
            }
            ObjectNode objectNode = ServiceUtils.convertToJSON(assayRows, "data");
            System.out.println("Assay rows to update: " + objectNode);
            updateRows(studyID, objectNode, assayFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRows(String studyID, ObjectNode json, String fileName) throws Exception {
        String url = mlProperties.getUrl() + studyID + "/rows/" + fileName;
        headers.set("user_token", this.apiKey);

        HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
        restTemplate.put(url, requestBody, new Object[]{});
    }
}
