package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.omg.CORBA.Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.metabolights.converters.USIContactsToMLContacts;
import uk.ac.ebi.subs.metabolights.converters.USIPublicationToMLPublication;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
public class DeletionService {

    private static final Logger logger = LoggerFactory.getLogger(DeletionService.class);

    private RestTemplate restTemplate;

    private MLProperties mlProperties;

    private HttpHeaders headers;



    private USIContactsToMLContacts usiContactsToMLContacts;
    private USIPublicationToMLPublication usiPublicationToMLPublication;


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
        mlProperties = new MLProperties();

        headers = new HttpHeaders();
        headers.set("user_token", mlProperties.getApiKey());
    }


    public void deletePublication(String studyID, uk.ac.ebi.subs.data.component.Publication publication) {
        if (publication == null) return;
        if (publication.getArticleTitle() == null) return;

        try {
            HttpEntity<String> requestParams = new HttpEntity<>("parameters", headers);
            String url = mlProperties.getUrl() + studyID + "/publications?title=" + publication.getArticleTitle();
            restTemplate.delete(url,requestParams);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
