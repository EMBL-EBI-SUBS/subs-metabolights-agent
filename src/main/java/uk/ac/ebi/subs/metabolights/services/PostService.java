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
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.metabolights.converters.USIContactsToMLContacts;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);


    private USIContactsToMLContacts usiContactsToMLContacts;

    private RestTemplate restTemplate;

    private MLProperties mlProperties;


    public PostService() {
        this.restTemplate = new RestTemplate();
        List messageConverters = this.restTemplate.getMessageConverters();
        messageConverters.add(new JsonAsTextPlainHttpMessageConverter());

        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        messageConverters.add(jsonHttpMessageConverter);
        this.restTemplate.setMessageConverters(messageConverters);

        usiContactsToMLContacts = new USIContactsToMLContacts();
        mlProperties = new MLProperties();
    }


    public uk.ac.ebi.subs.metabolights.model.Contact add(String studyID, Contact contact) {
        uk.ac.ebi.subs.metabolights.model.Contact addedContact = null;
        if (contact == null) return addedContact;
        try {
            ObjectNode contactsJSON = ServiceUtils.convertToJSON(usiContactsToMLContacts.convert(contact), "contact");
            HttpHeaders headers = new HttpHeaders();
            headers.set("user_token", mlProperties.getApiKey());

            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(contactsJSON, headers);
            String url = mlProperties.getUrl() + studyID + "/contacts";
            addedContact = restTemplate.postForObject(url, requestBody, uk.ac.ebi.subs.metabolights.model.Contact.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addedContact;
    }
}
