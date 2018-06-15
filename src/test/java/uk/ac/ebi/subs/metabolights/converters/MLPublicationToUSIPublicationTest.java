package uk.ac.ebi.subs.metabolights.converters;

import org.junit.Test;
import uk.ac.ebi.subs.data.component.Publication;

import static org.junit.Assert.assertEquals;

/**
 * Created by kalai on 19/12/2017.
 */
public class MLPublicationToUSIPublicationTest {
    @Test
    public void convert() throws Exception {
        MLPublicationToUSIPublication toUSIPublication = new MLPublicationToUSIPublication();
        uk.ac.ebi.subs.metabolights.model.Publication mlTestPublication =  Utilities.generateMLPublication();

        Publication publication = toUSIPublication.convert(mlTestPublication);
        USIPublicationToMLPublication toMLPublication = new USIPublicationToMLPublication();

        uk.ac.ebi.subs.metabolights.model.Publication mlPublication = toMLPublication.convert(publication);
        assertEquals(mlPublication,mlTestPublication);
    }

}