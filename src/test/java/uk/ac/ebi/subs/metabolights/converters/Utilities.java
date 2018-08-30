package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.io.IOUtils;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by kalai on 19/12/2017.
 */
public class Utilities {
    public static final ObjectMapper mapper = new ObjectMapper();

    public static uk.ac.ebi.subs.metabolights.model.Contact generateMLContact() {
        uk.ac.ebi.subs.metabolights.model.Contact contact = new uk.ac.ebi.subs.metabolights.model.Contact();
        contact.setAddress("Hinxton");
        contact.setAffiliation("EMBL-EBI");
        contact.setFax("+11123");
        contact.setEmail("test@abc.com");
        contact.setFirstName("Alice");
        contact.setLastName("Bob");
        return contact;
    }

    public static Contact generateUSIContact() {
        Contact contact = new Contact();
        contact.setAddress("Hinxton");
        contact.setAffiliation("EMBL-EBI");
        contact.setFax("+11123");
        contact.setPhone("+11125");
        contact.setEmail("testie10@abc.com");
        contact.setFirstName("Alice");
        contact.setLastName("Bob");
        contact.setMiddleInitials("");
        return contact;
    }

    public static uk.ac.ebi.subs.metabolights.model.Publication generateMLPublication() {
        uk.ac.ebi.subs.metabolights.model.Publication mlPublication = new uk.ac.ebi.subs.metabolights.model.Publication();
        mlPublication.setPubMedID("1000");
        mlPublication.setAuthorList("Bob, Alice, Tom");
        mlPublication.setTitle("New publication");
        mlPublication.setDoi("123-343-567-890");
        return mlPublication;
    }

    public static Publication generateUSIPublication() {
        Publication publication = new Publication();
        publication.setPubmedId("10000");
        publication.setAuthors("Bob, Alice, Tom");
        publication.setArticleTitle("New Test publication");
        publication.setDoi("123-343-567-890");
        return publication;
    }


    public static Sample generateUsiSample() {
        Sample usiSample = new Sample();
        usiSample.setAccession("SAM123");
        usiSample.setTeam(generateTeam());
        usiSample.setTaxon("Mus musculus");
        usiSample.setTaxonId(10090L);
        usiSample.setTitle("Experiment on mice.");
        usiSample.setDescription("Sample from Mus musculus.");
        usiSample.setAlias("This is an USI alias");
        usiSample.setReleaseDate(LocalDate.now());
        usiSample.setAttributes(generateUsiAttributes());
        usiSample.setSampleRelationships(Arrays.asList(generateUsiRelationship()));
        return usiSample;
    }

    public static Team generateTeam() {
        Team team = new Team();
        team.setName("self.usi-team");
        return team;
    }

    public static Map.Entry<String, Collection<Attribute>> generateUsiAttribute() {
        Map<String, Collection<Attribute>> usiAttributes = new HashMap<>();

        Attribute attribute = new Attribute();
        attribute.setValue("1.5");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        attribute.setTerms(Arrays.asList(term));
        attribute.setUnits("year");
        usiAttributes.put("age", Arrays.asList(attribute));

        return usiAttributes.entrySet().iterator().next();
    }

    public static List<uk.ac.ebi.subs.metabolights.model.Protocol> generateMLProtocols() {
        List<uk.ac.ebi.subs.metabolights.model.Protocol> mlProtocols = new ArrayList<>();
        uk.ac.ebi.subs.metabolights.model.Protocol protocol = new uk.ac.ebi.subs.metabolights.model.Protocol();
        protocol.setName("Extraction");
        protocol.setDescription("Test extraction");
        mlProtocols.add(protocol);
        return mlProtocols;
    }

    public static Map<String, Collection<Attribute>> generateUsiAttributes() {
        Map<String, Collection<Attribute>> usiAttributes = new HashMap<>();

        Attribute usiAttribute_1 = new Attribute();
        usiAttribute_1.setValue("1.5");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        usiAttribute_1.setTerms(Arrays.asList(term));
        usiAttribute_1.setUnits("year");
        usiAttributes.put("age", Arrays.asList(usiAttribute_1));

        Attribute usiAttribute_2 = new Attribute();
        usiAttribute_2.setValue(Instant.now().toString());
        usiAttributes.put("update", Arrays.asList(usiAttribute_2));

        Attribute usiAttribute_3 = new Attribute();
        usiAttribute_3.setValue("mouse");
        Term t = new Term();
        t.setUrl("http://purl.obolibrary.org/obo/NCBITaxon_10090");
        usiAttribute_3.setTerms(Arrays.asList(t));
        usiAttributes.put("synonym", Arrays.asList(usiAttribute_3));

        return usiAttributes;
    }

    public static SampleRelationship generateUsiRelationship() {
        SampleRelationship usiRelationship = new SampleRelationship();
        usiRelationship.setRelationshipNature("Child of");
        usiRelationship.setAccession("SAM990");
        return usiRelationship;
    }


    public static uk.ac.ebi.subs.metabolights.model.Study getMLStudyFromDisc() {
        uk.ac.ebi.subs.metabolights.model.Study study = new uk.ac.ebi.subs.metabolights.model.Study();
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/MTBLS2_isa.json"));
            uk.ac.ebi.subs.metabolights.model.Project project = new uk.ac.ebi.subs.metabolights.model.Project();
            try {
                mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
                project = mapper.readValue(result, uk.ac.ebi.subs.metabolights.model.Project.class);
                study = project.getStudies().get(0);
                return study;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return study;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static uk.ac.ebi.subs.metabolights.model.Project getMLProjectFromDisc() {
        uk.ac.ebi.subs.metabolights.model.Project project = new uk.ac.ebi.subs.metabolights.model.Project();
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/MTBLS2_isa.json"));
            try {
                mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
                project = mapper.readValue(result, uk.ac.ebi.subs.metabolights.model.Project.class);
                return project;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SubmissionEnvelope getUSISubmisisonFromDisc() {
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/MTBLS2_usi.json"));
            try {
                mapper.registerModule(new JavaTimeModule());
                submissionEnvelope = mapper.readValue(result, SubmissionEnvelope.class);
                return submissionEnvelope;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static uk.ac.ebi.subs.metabolights.model.Sample getMLSampleFromDisc() {
        uk.ac.ebi.subs.metabolights.model.Sample sample;
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/ML_Single_Sample.json"));
            try {
                mapper.registerModule(new JavaTimeModule());
                sample = mapper.readValue(result, uk.ac.ebi.subs.metabolights.model.Sample.class);
                return sample;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static uk.ac.ebi.subs.data.submittable.Sample getUSISampleFromDisc() {
        uk.ac.ebi.subs.data.submittable.Sample sample;
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/USI_Single_Sample.json"));
            try {
                mapper.registerModule(new JavaTimeModule());
                sample = mapper.readValue(result, uk.ac.ebi.subs.data.submittable.Sample.class);
                return sample;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<uk.ac.ebi.subs.validator.model.Submittable<Sample>> getUSISampleListFromDisc() {
        List<uk.ac.ebi.subs.data.submittable.Sample> samples;
        List<uk.ac.ebi.subs.validator.model.Submittable<Sample>> submittableSamples = new ArrayList<>();
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/MTBLS2_usi_sampleList.json"));
            try {
                mapper.registerModule(new JavaTimeModule());
                samples = mapper.readValue(result, new TypeReference<List<Sample>>(){});
                for(uk.ac.ebi.subs.data.submittable.Sample sample : samples){
                    submittableSamples.add(new uk.ac.ebi.subs.validator.model.Submittable<>(sample,"1"));
                }
                return submittableSamples;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static uk.ac.ebi.subs.validator.model.Submittable<Study> getUSIStudyFromDisc() {
        SubmissionEnvelope submissionEnvelope = getUSISubmisisonFromDisc();
        List<uk.ac.ebi.subs.data.submittable.Study> studies = submissionEnvelope.getStudies();
        return new Submittable<>(studies.get(0),submissionEnvelope.getSubmission().getId());
    }


}
