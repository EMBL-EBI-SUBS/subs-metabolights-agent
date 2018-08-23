package uk.ac.ebi.subs.metabolights.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
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

    public void updateContacts(String studyID, List<Contact> contacts){
        
    }
}
