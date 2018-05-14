package uk.ac.ebi.subs.metabolights.converters;

import org.junit.Test;
import uk.ac.ebi.subs.metabolights.model.Contact;
import uk.ac.ebi.subs.metabolights.model.Study;

import java.util.List;

/**
 * Created by kalai on 19/12/2017.
 */
public class MLStudyToUSIStudyTest {
    @Test
    public void convert() throws Exception {
        Study mlStudyFromDisc = WSUtils.getMLStudyFromDisc();
        List<Contact> people = mlStudyFromDisc.getPeople();
        for(Contact contact : people){
            System.out.println(contact.getFirstName());
        }


    }

}