package uk.ac.ebi.subs.metabolights.validator.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.metabolights.validator.schema.custom.JsonAsTextPlainHttpMessageConverter;

import java.util.List;

@Service
public class SchemaService {

    @Autowired
    private RestTemplate restTemplate;

    public SchemaService() {
        List messageConverters = this.restTemplate.getMessageConverters();
        messageConverters.add(new JsonAsTextPlainHttpMessageConverter());
        this.restTemplate.setMessageConverters(messageConverters);
    }

    public JsonNode getSchemaFor(String submittableType, String schemaUrl) {
        JsonNode schema;
        try {
            schema = restTemplate.getForObject(schemaUrl, ObjectNode.class);
        } catch (RestClientException e) {
            throw new RestClientException(submittableType + " schema - " + e.getMessage(), e);
        }
        return schema;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }
}
