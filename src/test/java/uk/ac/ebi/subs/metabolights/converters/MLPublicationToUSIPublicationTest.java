package uk.ac.ebi.subs.metabolights.converters;

import org.junit.Test;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.metabolights.model.StudyPublication;

import static org.junit.Assert.*;

/**
 * Created by kalai on 19/12/2017.
 */
public class MLPublicationToUSIPublicationTest {
    @Test
    public void convert() throws Exception {
        MLPublicationToUSIPublication toUSIPublication = new MLPublicationToUSIPublication();
        StudyPublication mlTestPublication =  Utilities.generateMLPublication();
        Publication publication = toUSIPublication.convert(mlTestPublication);
        USIPublicationToMLPublication toMLPublication = new USIPublicationToMLPublication();
        StudyPublication mlPublication = toMLPublication.convert(publication);
        assertEquals(mlPublication,mlTestPublication);
    }

}