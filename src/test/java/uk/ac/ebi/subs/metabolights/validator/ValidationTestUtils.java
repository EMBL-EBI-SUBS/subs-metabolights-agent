package uk.ac.ebi.subs.metabolights.validator;


import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.ArrayList;
import java.util.List;

public class ValidationTestUtils {

    public static List<Contact> generateContacts(){
        List<Contact> contacts = new ArrayList<>();
        Contact contact1 = new Contact();
        contact1.setAddress("Hinxton");
        contact1.setAffiliation("EMBL-EBI");
        contact1.setFax("+11123");
        contact1.setEmail("test@abc.com");
        contact1.setFirstName("Alice");
        contact1.setLastName("Bob");

        Contact contact2 = new Contact();
        contact2.setAddress("Hinxton");
        contact2.setAffiliation("EMBL-EBI");
        contact2.setFax("+11123");
        contact2.setEmail("");
        contact2.setFirstName("Alex");
        contact2.setLastName("Ben");

        contacts.add(contact1);
        contacts.add(contact2);
        return contacts;
    }

    public static List<Submittable<Protocol>> generateProtocols(){
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        Protocol protocol = new Protocol();
        protocol.setTitle("Sample collection");
        protocol.setDescription("");

        protocols.add(new Submittable<>(protocol,"1"));
        return protocols;
    }

    public static List<Submittable<Protocol>> generateProtocolsForImagingMS(){
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        protocols.addAll(getCommonProtocols());

        Protocol protocol = new Protocol();
        protocol.setTitle("Histology");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol,"1"));


        protocol = new Protocol();
        protocol.setTitle("Preparation");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol,"1"));


        return protocols;
    }

    public static List<Submittable<Protocol>> getCommonProtocols(){
        List<Submittable<Protocol>> protocols = new ArrayList<>();

        Protocol protocol = new Protocol();
        protocol.setTitle("Extraction");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol,"1"));


        protocol = new Protocol();
        protocol.setTitle("Data transformation");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol,"1"));


        protocol = new Protocol();
        protocol.setTitle("Metabolite identification");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol,"1"));


        return protocols;

    }

    public static List<Submittable<Protocol>> generateProtocolsForMS(){
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        protocols.addAll(getCommonProtocols());

        Protocol protocol = new Protocol();
        protocol.setTitle("Mass spectrometry");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol,"1"));

        protocol = new Protocol();
        protocol.setTitle("Chromatography");
        protocol.setDescription("This is test description");

        protocols.add(new Submittable<>(protocol,"1"));
        return protocols;
    }

    public static List<Submittable<Protocol>> generateProtocolsForImagingNMR(){
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        protocols.addAll(getCommonProtocols());

        Protocol nmrProtocol = new Protocol();
        nmrProtocol.setTitle("Magnetic resonance imaging");
        nmrProtocol.setDescription("This is test description");
        protocols.add(new Submittable<>(nmrProtocol,"1"));


        nmrProtocol = new Protocol();
        nmrProtocol.setTitle("In vivo magnetic resonance spectroscopy");
        nmrProtocol.setDescription("This is test description");
        protocols.add(new Submittable<>(nmrProtocol,"1"));


        nmrProtocol = new Protocol();
        nmrProtocol.setTitle("In vivo magnetic resonance assay");
        nmrProtocol.setDescription("This is test description");
        protocols.add(new Submittable<>(nmrProtocol,"1"));


        return protocols;
    }

    public static List<Submittable<Protocol>> generateProtocolsForImagingNMRWithMissingEntries(){
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        protocols.addAll(getCommonProtocols());

        Protocol nmrProtocol = new Protocol();
        nmrProtocol.setTitle("Magnetic resonance imaging");
        nmrProtocol.setDescription("This is test description");
        protocols.add(new Submittable<>(nmrProtocol,"1"));


        return protocols;
    }

    public static List<Submittable<Protocol>> generateProtocolsForNMR(){
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        protocols.addAll(getCommonProtocols());

        Protocol protocol = new Protocol();
        protocol.setTitle("NMR sample");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol,"1"));


        protocol = new Protocol();
        protocol.setTitle("NMR spectroscopy");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol,"1"));


        protocol = new Protocol();
        protocol.setTitle("NMR assay");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol,"1"));

        return protocols;
    }

    public static Project getProjectWithContacts(){
        Project project = new Project();
        project.setContacts(generateContacts());
        return project;
    }

    public static Project getProjectWithPublications(){
        Project project = new Project();
        project.setPublications(generatePublications());
        return project;
    }

    public static List<Publication> generatePublications(){
        List<Publication> publications = new ArrayList<>();
        Publication publication = new Publication();
        publication.setArticleTitle("This is a metabolomics test study");
        publication.setPubmedId("12345");
        publications.add(publication);
        return publications;
    }

}
