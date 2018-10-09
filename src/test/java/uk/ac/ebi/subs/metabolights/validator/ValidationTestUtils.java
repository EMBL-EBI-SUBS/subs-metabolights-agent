package uk.ac.ebi.subs.metabolights.validator;


import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.model.StudyAttributes;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.*;

public class ValidationTestUtils {

    public static List<Contact> generateContacts() {
        List<Contact> contacts = new ArrayList<>();
        Contact contact1 = new Contact();
        contact1.setAddress("Hinxton");
        contact1.setAffiliation("EMBL-EBI");
        contact1.setFax("+11123");
        contact1.setEmail("test@abc.com");
        contact1.setFirstName("Alice");
        contact1.setMiddleInitials("");
        contact1.setLastName("Bob");
        contact1.setPhone("12345");

        Contact contact2 = new Contact();
        contact2.setAddress("Hinxton");
        contact2.setAffiliation("EMBL-EBI");
        contact2.setFax("+11123");
        contact2.setEmail("");
        contact2.setFirstName("Alex");
        contact1.setMiddleInitials("");
        contact2.setLastName("Ben");
        contact1.setPhone("56879");

        contacts.add(contact1);
        contacts.add(contact2);
        return contacts;
    }

    public static List<Submittable<Protocol>> generateProtocols() {
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        Protocol protocol = new Protocol();
        protocol.setTitle("Sample collection");
        protocol.setDescription("");

        protocols.add(new Submittable<>(protocol, "1"));
        return protocols;
    }

    public static List<Submittable<Protocol>> generateProtocolsForImagingMS() {
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        protocols.addAll(getCommonProtocols());

        Protocol protocol = new Protocol();
        protocol.setTitle("Histology");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol, "1"));

        protocol = new Protocol();
        protocol.setTitle("Preparation");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol, "1"));
        return protocols;
    }

    public static List<Submittable<Protocol>> getCommonProtocols() {
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        Protocol protocol = new Protocol();
        protocol.setTitle("Extraction");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol, "1"));

        protocol = new Protocol();
        protocol.setTitle("Data transformation");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol, "1"));

        protocol = new Protocol();
        protocol.setTitle("Metabolite identification");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol, "1"));

        return protocols;

    }

    public static List<Submittable<Protocol>> getProtocols() {
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        Submittable<Protocol> protocolSubmittable = new Submittable<Protocol>();
        Protocol protocol = new Protocol();

        protocol.setTitle("Metabolite identification");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<Protocol>(protocol, "1"));

        protocol = new Protocol();
        protocol.setTitle("Data transformation");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<Protocol>(protocol, "1"));

        return protocols;
    }

    public static List<Submittable<Protocol>> generateProtocolsForMS() {
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        protocols.addAll(getCommonProtocols());

        Protocol protocol = new Protocol();
        protocol.setTitle("Mass spectrometry");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<Protocol>(protocol, "1"));

        protocol = new Protocol();
        protocol.setTitle("Chromatography");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<Protocol>(protocol, "1"));

        return protocols;
    }

    public static List<Submittable<Protocol>> generateProtocolsForImagingNMR() {
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        protocols.addAll(getCommonProtocols());

        Protocol nmrProtocol = new Protocol();
        nmrProtocol.setTitle("Magnetic resonance imaging");
        nmrProtocol.setDescription("This is test description");
        protocols.add(new Submittable<>(nmrProtocol, "1"));

        nmrProtocol = new Protocol();
        nmrProtocol.setTitle("In vivo magnetic resonance spectroscopy");
        nmrProtocol.setDescription("This is test description");
        protocols.add(new Submittable<>(nmrProtocol, "1"));

        nmrProtocol = new Protocol();
        nmrProtocol.setTitle("In vivo magnetic resonance assay");
        nmrProtocol.setDescription("This is test description");
        protocols.add(new Submittable<>(nmrProtocol, "1"));

        return protocols;
    }

    public static List<Submittable<Protocol>> generateProtocolsForImagingNMRWithMissingEntries() {
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        protocols.addAll(getCommonProtocols());

        Protocol nmrProtocol = new Protocol();
        nmrProtocol.setTitle("Magnetic resonance imaging");
        nmrProtocol.setDescription("This is test description");
        protocols.add(new Submittable<>(nmrProtocol, "1"));

        return protocols;
    }

    public static List<Submittable<Protocol>> generateProtocolsForNMR() {
        List<Submittable<Protocol>> protocols = new ArrayList<>();
        protocols.addAll(getCommonProtocols());

        Protocol protocol = new Protocol();
        protocol.setTitle("NMR sample");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol, "1"));

        protocol = new Protocol();
        protocol.setTitle("NMR spectroscopy");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol, "1"));

        protocol = new Protocol();
        protocol.setTitle("NMR assay");
        protocol.setDescription("This is test description");
        protocols.add(new Submittable<>(protocol, "1"));

        return protocols;
    }

    public static Submittable<Project> getProjectWithContactsAndPublications() {
        Project project = new Project();
        project.setContacts(generateContacts());
        project.setPublications(generatePublications());
        Submittable<Project> submittableProject = new Submittable<>(project, String.valueOf(UUID.randomUUID()));
        return submittableProject;
    }

    public static Project getProjectWithContacts() {
        Project project = new Project();
        project.setContacts(generateContacts());
        return project;
    }

    public static Project getProjectWithPublications() {
        Project project = new Project();
        project.setPublications(generatePublications());
        return project;
    }

    public static List<Publication> generatePublications() {
        List<Publication> publications = new ArrayList<>();
        Publication publication = new Publication();
        publication.setArticleTitle("This is a metabolomics test study");
        publication.setPubmedId("12345");
        publication.setPubmedId("12345-12345");
        publication.setAuthors("Bob, Bill, Jam");
        publications.add(publication);
        return publications;
    }

    public static Publication generatePublication() {
        Publication publication = new Publication();
        publication.setArticleTitle("This is a metabolomics test study");
        return publication;
    }

    public static List<File> getDataFiles() {
        List<File> dataFiles = new ArrayList<>();
        File file = new File();
        file.setName("m_mtbl2_metabolite profiling_mass spectrometry_v2_maf.tsv");
        file.setType("Metabolite Assignment File");
        dataFiles.add(file);
        return dataFiles;
    }

    public static AssayData getAssayData() {
        AssayData assayData = new AssayData();
        assayData.setFiles(getDataFiles());
        return assayData;
    }


    public static Attribute generateStudyFactorAttribute() {
        Attribute attribute = new Attribute();
        attribute.setValue("Test factor");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        attribute.setTerms(Arrays.asList(term));
        return attribute;
    }

    public static Attribute generateStudyDescriptorAttribute() {
        Attribute attribute = new Attribute();
        attribute.setValue("Test descriptor");
        return attribute;
    }

    public static Map<String, Collection<Attribute>> getStudyAttributes() {
        Map<String, Collection<Attribute>> usiAttributes = new HashMap<>();
        usiAttributes.put(StudyAttributes.STUDY_FACTORS, Arrays.asList(generateStudyFactorAttribute()));
        usiAttributes.put(StudyAttributes.STUDY_DESCRIPTORS, Arrays.asList(generateStudyDescriptorAttribute()));
        return usiAttributes;
    }


}
