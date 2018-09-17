package uk.ac.ebi.subs.metabolights.services;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.metabolights.converters.USISampleToMLSample;
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

    @Test
    public void addPublication() {
        Publication publication = Utilities.generateUSIPublication();
        String newTitle =  publication.getArticleTitle() + " - " + UUID.randomUUID().toString();
        publication.setArticleTitle(newTitle);
        uk.ac.ebi.subs.metabolights.model.Publication addedPublication = this.postService.add("MTBLS2", publication);
        assertEquals(addedPublication.getTitle(),newTitle);
    }

    @Test
    public void addProtocol() {
        Protocol protocol = Utilities.generateUSIProtocol();
        String newTitle =  protocol.getTitle() + " - " + UUID.randomUUID().toString();
        protocol.setTitle(newTitle);
        uk.ac.ebi.subs.metabolights.model.Protocol addedProtocol = this.postService.add("MTBLS2", protocol);
        assertEquals(addedProtocol.getName(),newTitle);
    }

    @Test
    public void addSample(){
        Sample usiSample = Utilities.getUSISampleFromDisc();
        String newAlias =  usiSample.getAlias() + " - " + UUID.randomUUID().toString();
        usiSample.setAlias(newAlias);

     //   USISampleToMLSample usiSampleToMLSample = new USISampleToMLSample();
     //   uk.ac.ebi.subs.metabolights.model.Sample mlSample = usiSampleToMLSample.convert(usiSample);
        this.postService.addSample("MTBLS2", usiSample);
        //assertEquals(addedSample.getName(),newTitle);
    }

}