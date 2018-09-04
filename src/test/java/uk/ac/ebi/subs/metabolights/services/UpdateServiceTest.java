package uk.ac.ebi.subs.metabolights.services;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.converters.*;
import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

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

    public void updateFactor(){
        Attribute attribute = Utilities.generateSingleUSIAttribute();
        String newName =  attribute.getValue() + " - " + UUID.randomUUID().toString();
        attribute.setValue(newName);
        Factor factor = this.postService.addFactor("MTBLS2", attribute);
        System.out.println(newName);

        factor.getFactorType().setTermAccession("Modified url");

        MLFactorToUSIFactor mlFactorToUSIFactor = new MLFactorToUSIFactor();
        this.updateService.updateFactor("MTBLS2", mlFactorToUSIFactor.convert(factor));
        System.out.println("deleted - " +  newName);
    }
    
    public void updateDescriptor(){
        Attribute attribute = Utilities.generateSingleUSIAttribute();
        String newName =  attribute.getValue() + " - " + UUID.randomUUID().toString();
        attribute.setValue(newName);
        OntologyModel descriptor = this.postService.addDescriptor("MTBLS2", attribute);
        System.out.println(newName);

        descriptor.setTermAccession("Modified url");

        MLDescriptorToUSIDescriptor mlDescriptorToUSIDescriptor = new MLDescriptorToUSIDescriptor();
        this.updateService.updateDescriptor("MTBLS2", mlDescriptorToUSIDescriptor.convert(descriptor));
        System.out.println("deleted - " +  newName);
    }
}