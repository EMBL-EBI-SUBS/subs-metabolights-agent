package uk.ac.ebi.subs.metabolights.services;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.metabolights.converters.MLContactsToUSIContacts;
import uk.ac.ebi.subs.metabolights.converters.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class UpdateServiceTest {

    private UpdateService updateService;
    private PostService postService;

    @Before
    public void setUp() {
        this.updateService = new UpdateService();
        this.postService = new PostService();
    }


    @Test
    public void updateContact() {
        Contact contact = Utilities.generateUSIContact();
        contact.setEmail(UUID.randomUUID().toString() + "@dummy.com");
        uk.ac.ebi.subs.metabolights.model.Contact addedContact = this.postService.add("MTBLS2", contact);

        addedContact.setFirstName("Changed");
        MLContactsToUSIContacts converter = new MLContactsToUSIContacts();
        this.updateService.updateContact("MTBLS2", converter.convert(addedContact));
    }
}