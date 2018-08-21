package uk.ac.ebi.subs.metabolights.services;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class MLProperties {
    @Value("${metabolights.client.apiKey}")
    private String apiKey;
}
