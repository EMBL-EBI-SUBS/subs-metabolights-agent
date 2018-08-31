package uk.ac.ebi.subs.metabolights.services;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.converters.MLContactsToUSIContacts;
import uk.ac.ebi.subs.metabolights.converters.MLProtocolToUSIProtocol;
import uk.ac.ebi.subs.metabolights.converters.MLPublicationToUSIPublication;
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


    public void updateContact() {
        Contact contact = Utilities.generateUSIContact();
        contact.setEmail(UUID.randomUUID().toString() + "@dummy.com");
        uk.ac.ebi.subs.metabolights.model.Contact addedContact = this.postService.add("MTBLS2", contact);

        addedContact.setFirstName("Changed");
        MLContactsToUSIContacts converter = new MLContactsToUSIContacts();
        this.updateService.updateContact("MTBLS2", converter.convert(addedContact));
    }

    public void updatePublication() {
        Publication publication = Utilities.generateUSIPublication();
        String newTitle =  publication.getArticleTitle() + " - " + UUID.randomUUID().toString();
        publication.setArticleTitle(newTitle);
        uk.ac.ebi.subs.metabolights.model.Publication modified = this.postService.add("MTBLS2", publication);
        System.out.println(newTitle);

        modified.setAuthorList("ALice and BOB only");
        MLPublicationToUSIPublication converter = new MLPublicationToUSIPublication();
        this.updateService.updatePublication("MTBLS2", converter.convert(modified));
    }
    
    public void updateProtocol(){
        Protocol protocol = Utilities.generateUSIProtocol();
        String newTitle =  protocol.getTitle() + " - " + UUID.randomUUID().toString();
        protocol.setTitle(newTitle);
        uk.ac.ebi.subs.metabolights.model.Protocol modified = this.postService.add("MTBLS2", protocol);

        MLProtocolToUSIProtocol converter = new MLProtocolToUSIProtocol();
        this.updateService.updateProtocol("MTBLS2", converter.convert(modified));
    }
}