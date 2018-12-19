package uk.ac.ebi.subs.metabolights.services;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class MLProperties {

    @Value("${metabolights.apiKey}")
    private String apiKey;

    private String url = "http://wwwdev.ebi.ac.uk:80/metabolights/swagger/ws/studies";


}
