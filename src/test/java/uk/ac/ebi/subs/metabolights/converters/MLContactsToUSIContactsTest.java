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
        Contact usiContact = toUsiContacts.convert(Utilities.generateMLContact());
        assertEquals(usiContact.getEmail(), Utilities.generateMLContact().getEmail());
    }
}