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

    @Test
    public void convert() {
        Sample usiSample = Utilities.generateUsiSample();
        USISampleToMLSample sampleConverter = new USISampleToMLSample();
        MLSampleToUSISample mlSampleToUSISample = new MLSampleToUSISample();
        uk.ac.ebi.subs.metabolights.model.Sample mlSample = sampleConverter.convert(usiSample);

        Sample convertedUSI = mlSampleToUSISample.convert(mlSample);
        assertEquals(convertedUSI.getAlias(), "This is an USI alias");
    }

    @Test
    public void testWSSample() {
        uk.ac.ebi.subs.metabolights.model.Sample mlSample = Utilities.getMLSampleFromDisc();
        MLSampleToUSISample mlSampleToUSISample = new MLSampleToUSISample();

        Sample usiSample = mlSampleToUSISample.convert(mlSample);
        assertEquals(usiSample.getTaxon(), "Homo sapiens");
    }

    @Test
    public void testUSIToMLSample() {
        Sample usiSample = Utilities.getUSISampleFromDisc();
        USISampleToMLSample usiSampleToMLSample = new USISampleToMLSample();

        uk.ac.ebi.subs.metabolights.model.Sample mlSample = usiSampleToMLSample.convert(usiSample);
        assertEquals(mlSample.getDerivesFrom().get(0).getCharacteristics().get(0).getValue().getAnnotationValue(), "Synechocystis sp. PCC 6803");
    }

    @Test
    public void testMLToUSISample() {
        uk.ac.ebi.subs.metabolights.model.Sample mlSample = Utilities.getMLSampleFromDisc();
        MLSampleToUSISample mlSampleToUSISample = new MLSampleToUSISample();

        Sample converted = mlSampleToUSISample.convert(mlSample);
        assertEquals(converted.getTaxonId().toString(), "9606");
    }

}
