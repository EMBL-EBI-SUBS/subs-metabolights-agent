package uk.ac.ebi.subs.metabolights.services;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.metabolights.model.Project;
import uk.ac.ebi.subs.metabolights.model.Study;

@Service
public class FetchService {
    private static final Logger logger = LoggerFactory.getLogger(FetchService.class);

    private static final String METABOLIGHTS_API = "http://ves-ebi-8d:5000/mtbls/ws/";

    private RestTemplate restTemplate;

    public FetchService(){
         this.restTemplate = new RestTemplate();
    }

    public Study getStudy(String accession) {

        Project project = restTemplate.getForObject(METABOLIGHTS_API + "studies/" + accession, Project.class);
        if (project != null) {
            return project.getStudies() != null && project.getStudies().size() > 0 ? project.getStudies().get(0) : null;
        }
        return null;
    }

}
