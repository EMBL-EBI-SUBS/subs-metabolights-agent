package uk.ac.ebi.subs.metabolights.agent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.MetaboLightsAgentApplication;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.metabolights.converters.Utilities;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


@SpringBootTest(classes = {
        MetaboLightsAgentApplication.class})
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)

public class AgentProcessorUtilsTest {

    @Test
    public void getAssayRowsToAddAndUpdate() {
        try {
            Map<String, List<Assay>> assayRowsToAddAndUpdate = AgentProcessorUtils.getAssayRowsToAddAndUpdate(Utilities.getUSIAssayListFromDisc(), Utilities.getTestNmrMetabolightsTableFromDisc());
           for(Map.Entry<String, List<Assay>> entry : assayRowsToAddAndUpdate.entrySet()){
               System.out.println(entry.getKey() + "-" + + entry.getValue().size());
           }
           assertEquals(assayRowsToAddAndUpdate.get("add").size(), 1);
            assertEquals(assayRowsToAddAndUpdate.get("update").size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}