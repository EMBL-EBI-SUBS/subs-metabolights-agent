package uk.ac.ebi.subs.metabolights.services;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.metabolights.converters.USIContactsToMLContacts;
import uk.ac.ebi.subs.metabolights.model.Study;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
public class UpdateService {

    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    private static final String METABOLIGHTS_API = "http://ves-ebi-90:5000/metabolights/ws/studies/";

    private RestTemplate restTemplate;

    private MLProperties mlProperties;

    @Autowired
    private FetchService fetchService;

    private USIContactsToMLContacts usiContactsToMLContacts;


    public UpdateService() {
        this.restTemplate = new RestTemplate();
        List messageConverters = this.restTemplate.getMessageConverters();
        messageConverters.add(new JsonAsTextPlainHttpMessageConverter());
        this.restTemplate.setMessageConverters(messageConverters);
        usiContactsToMLContacts = new USIContactsToMLContacts();
    }

    public void updateContacts(String studyID, List<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) return;
        JSONObject json = new JSONObject();
        try {
            json.put("contacts" , usiContactsToMLContacts.convert(contacts));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("user_token", mlProperties.getApiKey());

        HttpEntity<JSONObject> requestBody = new HttpEntity<>(json, headers);
        restTemplate.put(mlProperties.getUrl() + studyID + "/contacts", requestBody, new Object[] {});

    }
}
