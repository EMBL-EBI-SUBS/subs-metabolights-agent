package uk.ac.ebi.subs.metabolights.services;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(ignoreUnknownFields = false)
@Data
public class MLProperties {
    private String url;
    private String status;
}
