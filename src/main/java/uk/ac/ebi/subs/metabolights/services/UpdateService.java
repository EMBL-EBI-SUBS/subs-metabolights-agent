package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.metabolights.converters.USIContactsToMLContacts;
import uk.ac.ebi.subs.metabolights.model.Study;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
public class UpdateService {

    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    private RestTemplate restTemplate;

    private MLProperties mlProperties;

    @Autowired
    private FetchService fetchService;

    private USIContactsToMLContacts usiContactsToMLContacts;


    public UpdateService() {
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

    public void updateContact(String studyID, Contact contact) {
        if (contact == null) return;
        if (contact.getEmail() == null) return;

        try {
            ObjectNode contactToUpdateJSON = ServiceUtils.convertToJSON(usiContactsToMLContacts.convert(contact), "contact");
            HttpHeaders headers = new HttpHeaders();
            headers.set("user_token", mlProperties.getApiKey());

            HttpEntity<ObjectNode> requestBody = new HttpEntity<>(contactToUpdateJSON, headers);
            String url = mlProperties.getUrl() + studyID + "/contacts?email=" + contact.getEmail();
            restTemplate.put(url, requestBody, new Object[]{});

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
