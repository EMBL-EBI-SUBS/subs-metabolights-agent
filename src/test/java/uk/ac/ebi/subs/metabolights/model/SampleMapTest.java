package uk.ac.ebi.subs.metabolights.model;

import org.junit.Test;
import uk.ac.ebi.subs.metabolights.converters.MLSampleToUSISample;
import uk.ac.ebi.subs.metabolights.converters.USISampleToMLSample;
import uk.ac.ebi.subs.metabolights.converters.Utilities;

import java.util.Map;

import static org.junit.Assert.*;

public class SampleMapTest {
    @Test
    public void testProcess() {
        uk.ac.ebi.subs.data.submittable.Sample usiSample = Utilities.getUSISampleFromDisc();
        USISampleToMLSample usiSampleToMLSample = new USISampleToMLSample();

        uk.ac.ebi.subs.metabolights.model.Sample mlSample = usiSampleToMLSample.convert(usiSample);
        SampleMap mlSampleTableEntry = new SampleMap(mlSample);
        for(Map.Entry<String,String> entry : mlSampleTableEntry.entrySet()){
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

}