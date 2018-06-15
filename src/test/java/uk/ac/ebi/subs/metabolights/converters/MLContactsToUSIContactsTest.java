package uk.ac.ebi.subs.metabolights.converters;

import org.junit.Test;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.metabolights.model.Study;

import static org.junit.Assert.assertEquals;

/**
 * Created by kalai on 19/12/2017.
 */
public class MLContactsToUSIContactsTest {
    @Test
    public void convert() throws Exception {
      MLContactsToUSIContacts toUsiContacts = new MLContactsToUSIContacts();
        USIContactsToMLContacts toMLContacts = new USIContactsToMLContacts();
//      StudyContact mlTestContact =  Utilities.generateMLContacts();
//      Contact contact = toUsiContacts.convert(mlTestContact);
//      StudyContact mlContacts = toMLContacts.convert(contact);
//      assertEquals(mlContacts,mlTestContact);

      // test WS object
        Study study = WSUtils.getMLStudy("MTBLS2");
        for(uk.ac.ebi.subs.metabolights.model.Contact studyContact : study.getPeople()){
            System.out.println("Testing ... ");
            Contact usiContact = toUsiContacts.convert(studyContact);
            uk.ac.ebi.subs.metabolights.model.Contact mlContacts2 = toMLContacts.convert(usiContact);
            // todo fix Roles.
            assertEquals(mlContacts2.getAddress(),studyContact.getAddress());
        }
    }
}