package uk.ac.ebi.subs.metabolights.services;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.converters.MLProtocolToUSIProtocol;
import uk.ac.ebi.subs.metabolights.converters.MLPublicationToUSIPublication;
import uk.ac.ebi.subs.metabolights.converters.Utilities;

import java.util.UUID;

import static org.junit.Assert.*;

public class DeletionServiceTest {

    private DeletionService deletionService;
    private PostService postService;

    @Before
    public void setUp(){
        this.deletionService = new DeletionService();
        this.postService = new PostService();
    }

    @Test
    public void deletePublication() {
        Publication publication = Utilities.generateUSIPublication();
        String newTitle =  publication.getArticleTitle() + " - " + UUID.randomUUID().toString();
        publication.setArticleTitle(newTitle);
        uk.ac.ebi.subs.metabolights.model.Publication modified = this.postService.add("MTBLS2", publication);
        System.out.println(newTitle);

        MLPublicationToUSIPublication converter = new MLPublicationToUSIPublication();
        this.deletionService.deletePublication("MTBLS2", converter.convert(modified));
        System.out.println("deleted - " +  newTitle);
    }

    @Test
    public void deleteProtocol() {
        Protocol protocol = Utilities.generateUSIProtocol();
        String newTitle =  protocol.getTitle() + " - " + UUID.randomUUID().toString();
        protocol.setTitle(newTitle);
        uk.ac.ebi.subs.metabolights.model.Protocol modified = this.postService.add("MTBLS2", protocol);
        System.out.println(newTitle);

        MLProtocolToUSIProtocol converter = new MLProtocolToUSIProtocol();
        this.deletionService.deleteProtocol("MTBLS2", converter.convert(modified));
        System.out.println("deleted - " +  newTitle);
    }
}