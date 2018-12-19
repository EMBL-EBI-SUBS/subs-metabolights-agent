package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.metabolights.model.NMRAssayMap;

import static org.junit.Assert.*;

public class USIAssayToMLNMRAssayTableTest {

    @Test
    public void convert() throws JsonProcessingException {
        Assay usiAssay = Utilities.getUSIAssayFromDisc();
        USIAssayToMLNMRAssayTable usiAssayToMLNMRAssayTable = new USIAssayToMLNMRAssayTable();
        NMRAssayMap nmrAssayMap = usiAssayToMLNMRAssayTable.convert(usiAssay);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(nmrAssayMap));
        assertEquals(nmrAssayMap.size(), 43);
    }
}