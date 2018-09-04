package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.omg.CORBA.Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.converters.USIContactsToMLContacts;
import uk.ac.ebi.subs.metabolights.converters.USIProtocolToMLProtocol;
import uk.ac.ebi.subs.metabolights.converters.USIPublicationToMLPublication;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeletionService {

    private static final Logger logger = LoggerFactory.getLogger(DeletionService.class);

    private RestTemplate restTemplate;

    private MLProperties mlProperties;

    private HttpHeaders headers;


    private USIContactsToMLContacts usiContactsToMLContacts;
    private USIPublicationToMLPublication usiPublicationToMLPublication;
    private USIProtocolToMLProtocol usiProtocolToMLProtocol;


    public DeletionService() {
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
        mlProperties = new MLProperties();

        headers = new HttpHeaders();
        headers.set("user_token", mlProperties.getApiKey());
    }


    public void deletePublication(String studyID, uk.ac.ebi.subs.data.component.Publication publication) {
        if (publication == null) return;
        if (publication.getArticleTitle() == null) return;

        try {
            String url = mlProperties.getUrl() + studyID + "/publications?title=" + publication.getArticleTitle();
            HttpEntity<?> request = new HttpEntity<Object>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteProtocol(String studyID, Protocol protocol) {
        if (protocol == null) return;
        if (protocol.getTitle() == null) return;

        try {
             String url = mlProperties.getUrl() + studyID + "/protocols?name=" + protocol.getTitle();
             HttpEntity<?> request = new HttpEntity<Object>(headers);
             restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFactor(String studyID, Attribute attribute) {
        if (attribute == null) return;
        if (attribute.getValue() == null) return;

        try {
            String url = mlProperties.getUrl() + studyID + "/factors?name=" + attribute.getValue();
            HttpEntity<?> request = new HttpEntity<Object>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDescriptor(String studyID, Attribute attribute) {
        if (attribute == null) return;
        if (attribute.getValue() == null) return;

        try {
            String url = mlProperties.getUrl() + studyID + "/descriptors?term=" + attribute.getValue();
            HttpEntity<?> request = new HttpEntity<Object>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
