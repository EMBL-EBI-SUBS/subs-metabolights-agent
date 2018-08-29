package uk.ac.ebi.subs.metabolights.services;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.metabolights.converters.Utilities;

import java.util.UUID;

import static org.junit.Assert.*;

public class PostServiceTest {


    private PostService postService;

    @Before
    public void setUp(){
        this.postService = new PostService();
    }


    @Test
    public void addContact() {
        Contact contact = Utilities.generateUSIContact();
        contact.setEmail(UUID.randomUUID().toString() + "@dummy.com");
        uk.ac.ebi.subs.metabolights.model.Contact addedContact = this.postService.add("MTBLS2", contact);
        assertEquals(contact.getEmail(),addedContact.getEmail());
    }

}