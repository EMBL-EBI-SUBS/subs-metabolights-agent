package uk.ac.ebi.subs.metabolights.services;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.converters.*;
import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

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

    @Test
    public void deleteFactor(){
        Attribute attribute = Utilities.generateSingleUSIAttribute();
        String newName =  attribute.getValue() + " - " + UUID.randomUUID().toString();
        attribute.setValue(newName);
        Factor factor = this.postService.addFactor("MTBLS2", attribute);
        System.out.println(newName);

        MLFactorToUSIFactor mlFactorToUSIFactor = new MLFactorToUSIFactor();
        this.deletionService.deleteFactor("MTBLS2", mlFactorToUSIFactor.convert(factor));
        System.out.println("deleted - " +  newName);
    }

    @Test
    public void deleteDescriptor(){
        Attribute attribute = Utilities.generateSingleUSIAttribute();
        String newName =  attribute.getValue() + " - " + UUID.randomUUID().toString();
        attribute.setValue(newName);
        OntologyModel descriptor = this.postService.addDescriptor("MTBLS2", attribute);
        System.out.println(newName);

        MLDescriptorToUSIDescriptor mlDescriptorToUSIDescriptor = new MLDescriptorToUSIDescriptor();
        this.deletionService.deleteDescriptor("MTBLS2", mlDescriptorToUSIDescriptor.convert(descriptor));
        System.out.println("deleted - " +  newName);
    }
}