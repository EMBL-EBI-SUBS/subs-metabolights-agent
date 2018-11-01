package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.omg.CORBA.Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.converters.USIContactsToMLContacts;
import uk.ac.ebi.subs.metabolights.converters.USIProtocolToMLProtocol;
import uk.ac.ebi.subs.metabolights.converters.USIPublicationToMLPublication;
import uk.ac.ebi.subs.metabolights.model.Contact;
import uk.ac.ebi.subs.metabolights.model.Publication;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
public class DeletionService {

    private static final Logger logger = LoggerFactory.getLogger(DeletionService.class);

    private RestTemplate restTemplate;

    private MLProperties mlProperties;

    @Value("${metabolights.apiKey}")
    private String apiKey;


    private USIContactsToMLContacts usiContactsToMLContacts;
    private USIPublicationToMLPublication usiPublicationToMLPublication;
    private USIProtocolToMLProtocol usiProtocolToMLProtocol;

    private HttpHeaders headers;


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
        headers.set("save_audit_copy", "false");
    }


    public void deletePublication(String studyID, String title) {
        if (title == null || title.isEmpty()) return;

        try {
            String url = mlProperties.getUrl() + studyID + "/publications?title=" + title;
            headers.set("user_token", this.apiKey);
            HttpEntity<?> request = new HttpEntity<Object>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void deleteContact(String studyID, String email) {
        if (email == null || email.isEmpty()) return;

        try {
            String url = mlProperties.getUrl() + studyID + "/contacts?email=" + email;
            headers.set("user_token", this.apiKey);
            HttpEntity<?> request = new HttpEntity<Object>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void deleteProtocol(String studyID, Protocol protocol) {
        if (protocol == null) return;
        if (protocol.getTitle() == null) return;

        try {
            String url = mlProperties.getUrl() + studyID + "/protocols?name=" + protocol.getTitle();
            headers.set("user_token", this.apiKey);
            HttpEntity<?> request = new HttpEntity<Object>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void deleteFactor(String studyID, String factorName) {
        if (factorName == null || factorName.isEmpty()) return;

        try {
            String url = mlProperties.getUrl() + studyID + "/factors?name=" + factorName;
            headers.set("user_token", this.apiKey);
            HttpEntity<?> request = new HttpEntity<Object>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void deleteDescriptor(String studyID, String descriptorName) {
        if (descriptorName == null || descriptorName.isEmpty()) return;
        try {
            String url = mlProperties.getUrl() + studyID + "/descriptors?term=" + descriptorName;
            headers.set("user_token", this.apiKey);
            HttpEntity<?> request = new HttpEntity<Object>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void deleteMarkedSamples(String studyID) {
        try {
            String url = mlProperties.getUrl() + studyID + "/samples";
            headers.set("user_token", this.apiKey);
            HttpEntity<?> request = new HttpEntity<Object>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void deleteSampleRows(String studyID, String sampleFileName, List<Integer> sampleRows) {
        try {
            String url = mlProperties.getUrl() + studyID + "/samples/" + sampleFileName + "?row_num=" + ServiceUtils.getAsConcatenatedString(sampleRows);
            headers.set("user_token", this.apiKey);
            HttpEntity<?> request = new HttpEntity<Object>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public void deleteTableRows(String studyID, String filename, List<Integer> rowIndices) {
        try {
            String url = mlProperties.getUrl() + studyID + "/deleteRows/" + filename + "?row_num=" + ServiceUtils.getAsConcatenatedString(rowIndices);
            headers.set("user_token", this.apiKey);
            HttpEntity<?> request = new HttpEntity<Object>(headers);
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class, 1);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }
}
