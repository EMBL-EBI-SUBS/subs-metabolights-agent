package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.metabolights.model.Study;

import static org.junit.Assert.*;

@SpringBootTest(classes = {
        FetchService.class} )
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FetchServiceTest {

    @Autowired
    private FetchService fetchService;

    @Test
    public void getStudy() {
        Study mlStudy = this.fetchService.getStudy("MTBLS2");
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(mlStudy.getPeople());
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertTrue(!mlStudy.getDescription().isEmpty());
    }

    @Test
    public void createNewStudyAndGetAccession() {
        String accession = this.fetchService.createNewStudyAndGetAccession();
        assertTrue(accession.contains("MTBLS"));
    }

    @Test
    public void createNewNMRStudy() {
        String accession = this.fetchService.createNewStudyAndGetAccession();
        this.fetchService.cloneNMRTemplates(accession);
        assertTrue(accession.contains("MTBLS"));
    }
}
