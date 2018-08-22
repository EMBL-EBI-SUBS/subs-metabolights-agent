package uk.ac.ebi.subs.metabolights.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.metabolights.model.Study;

import static org.junit.Assert.*;

@SpringBootTest(classes = {
        FetchService.class} )
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FetchServiceTest {

    private FetchService fetchService;

    @Before
    public void setUp(){
        this.fetchService = new FetchService();
    }

    @Test
    public void getStudy() {
        Study mlStudy = this.fetchService.getStudy("MTBLS2");
        assertEquals(mlStudy.getPublicReleaseDate(),"2012-05-22");
    }

    @Test
    public void createNewStudyAndGetAccession() {
        String accession = this.fetchService.createNewStudyAndGetAccession();
        assertTrue(accession.contains("MTBLS"));
    }
}
