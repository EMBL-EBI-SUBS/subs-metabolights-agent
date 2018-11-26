package uk.ac.ebi.subs.metabolights.converters;

import org.junit.Test;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.metabolights.model.NMRAssayMap;

import static org.junit.Assert.*;

public class USIAssayToMLNMRAssayTableTest {

    @Test
    public void convert() {
        Assay usiAssay = Utilities.getUSIAssayFromDisc();
        USIAssayToMLNMRAssayTable usiAssayToMLNMRAssayTable = new USIAssayToMLNMRAssayTable();
        NMRAssayMap nmrAssayMap = usiAssayToMLNMRAssayTable.convert(usiAssay);
        assertEquals(nmrAssayMap.size(), 34);
    }
}