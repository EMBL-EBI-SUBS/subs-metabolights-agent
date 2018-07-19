package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.ac.ebi.subs.data.submittable.Sample;

import static org.junit.Assert.assertEquals;

/**
 * Created by kalai on 01/02/2018.
 */
public class USISampleToMLSampleTest {
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void convert() throws Exception {
        Sample usiSample = Utilities.generateUsiSample();
        USISampleToMLSample sampleConverter = new USISampleToMLSample();
        MLSampleToUSISample mlSampleToUSISample = new MLSampleToUSISample();
        uk.ac.ebi.subs.metabolights.model.Sample mlSample = sampleConverter.convert(usiSample);
      //  assertEquals("Mus musculus",mlSample.getDerives_from().get(0).getCharacteristics().get(0).getValue().getAnnotationValue());

        String mlSampleJSON = mapper.writeValueAsString(mlSample);
        System.out.println(mlSampleJSON);

        System.out.println("-----------");

        Sample convertedUSI = mlSampleToUSISample.convert(mlSample);
        String usiSampleJSON = mapper.writeValueAsString(convertedUSI);
        System.out.println(usiSampleJSON);
        assertEquals(usiSample.getTaxonId(),convertedUSI.getTaxonId());

        testWSSample();

    }

    @Test
    public void testWSSample() throws JsonProcessingException {
        System.out.println("WS test");
        uk.ac.ebi.subs.metabolights.model.Sample mlSample = WSUtils.getMLSampleFromDisc();
        MLSampleToUSISample mlSampleToUSISample = new MLSampleToUSISample();

        Sample usiSample = mlSampleToUSISample.convert(mlSample);
        String usiSampleJson = mapper.writeValueAsString(usiSample);
        System.out.println(usiSampleJson);
        assertEquals(usiSample.getTaxon(), "Homo sapiens");
        System.out.println("Its true");
    }

    @Test
    public void testMLToUSISample() throws JsonProcessingException {
        System.out.println("WS test");
        Sample usiSample = WSUtils.getUSISampleFromDisc();
        USISampleToMLSample usiSampleToMLSample = new USISampleToMLSample();

        uk.ac.ebi.subs.metabolights.model.Sample mlSample = usiSampleToMLSample.convert(usiSample);
        String mlSampleJson = mapper.writeValueAsString(mlSample);
        System.out.println(mlSampleJson);
        assertEquals(usiSample.getTaxon(), "Homo sapiens");
    }

}
