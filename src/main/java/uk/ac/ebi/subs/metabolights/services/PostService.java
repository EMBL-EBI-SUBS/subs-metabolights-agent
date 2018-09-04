package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.metabolights.converters.USIContactsToMLContacts;
import uk.ac.ebi.subs.metabolights.converters.USIFactorToMLFactor;
import uk.ac.ebi.subs.metabolights.converters.USIProtocolToMLProtocol;
import uk.ac.ebi.subs.metabolights.converters.USIPublicationToMLPublication;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);


    private USIContactsToMLContacts usiContactsToMLContacts;
    private USIPublicationToMLPublication usiPublicationToMLPublication;
    private USIProtocolToMLProtocol usiProtocolToMLProtocol;
    private USIFactorToMLFactor usiFactorToMLFactor;

    private RestTemplate restTemplate;

    private MLProperties mlProperties;

    private  HttpHeaders headers;


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
        
        mlProperties = new MLProperties();

        headers = new HttpHeaders();
        headers.set("user_token", mlProperties.getApiKey());
    }


    public uk.ac.ebi.subs.metabolights.model.Contact add(String studyID, Contact contact) {
        uk.ac.ebi.subs.metabolights.model.Contact addedContact = null;
        if (contact == null) return addedContact;
        try {
            ObjectNode contactsJSON = ServiceUtils.convertToJSON(usiContactsToMLContacts.convert(contact), "contact");
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(contactsJSON, headers);
            String url = mlProperties.getUrl() + studyID + "/contacts";
            addedContact = restTemplate.postForObject(url, requestBody, uk.ac.ebi.subs.metabolights.model.Contact.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addedContact;
    }

    public uk.ac.ebi.subs.metabolights.model.Publication add(String studyID, Publication publication) {
        uk.ac.ebi.subs.metabolights.model.Publication addedpublication = null;
        if (publication == null) return addedpublication;
        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiPublicationToMLPublication.convert(publication), "publication");
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);

            String url = mlProperties.getUrl() + studyID + "/publications";
            addedpublication = restTemplate.postForObject(url, requestBody, uk.ac.ebi.subs.metabolights.model.Publication.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addedpublication;
    }

    public uk.ac.ebi.subs.metabolights.model.Protocol add(String studyID, uk.ac.ebi.subs.data.submittable.Protocol protocol) {
        uk.ac.ebi.subs.metabolights.model.Protocol addedProtocol = null;
        if (protocol == null) return addedProtocol;
        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiProtocolToMLProtocol.convert(protocol), "protocol");
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);
           
            String url = mlProperties.getUrl() + studyID + "/protocols";
            addedProtocol = restTemplate.postForObject(url, requestBody, uk.ac.ebi.subs.metabolights.model.Protocol.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addedProtocol;
    }

    public uk.ac.ebi.subs.metabolights.model.Factor add(String studyID, Attribute attribute) {
        uk.ac.ebi.subs.metabolights.model.Factor addedFactor = null;
        if (attribute == null) return addedFactor;
        try {
            ObjectNode json = ServiceUtils.convertToJSON(usiFactorToMLFactor.convert(attribute), "factor");
            System.out.println("JSON = " + json);
            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(json, headers);

            String url = mlProperties.getUrl() + studyID + "/factors";
            addedFactor = restTemplate.postForObject(url, requestBody, uk.ac.ebi.subs.metabolights.model.Factor.class);
            System.out.println(addedFactor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addedFactor;
    }
}
