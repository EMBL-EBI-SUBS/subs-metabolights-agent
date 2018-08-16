package uk.ac.ebi.subs.metabolights.services;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
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
        assertEquals(mlStudy.getTitle(),"Metabolome phenotyping of inorganic carbon limitation in cells of the wild type and photorespiratory mutants of the cyanobacterium Synechocystis sp. strain PCC 6803.");

    }
}