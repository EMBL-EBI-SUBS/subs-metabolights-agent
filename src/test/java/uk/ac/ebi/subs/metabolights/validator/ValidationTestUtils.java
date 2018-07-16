package uk.ac.ebi.subs.metabolights.validator;


import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Protocol;

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

    public static List<Protocol> generateProtocols(){
        List<Protocol> protocols = new ArrayList<>();
        Protocol protocol = new Protocol();
        protocol.setTitle("Sample collection");
        protocol.setDescription("");

        protocols.add(protocol);
        return protocols;
    }

    public static Project getProjectWithContacts(){
        Project project = new Project();
        project.setContacts(generateContacts());
        return project;
    }

}
