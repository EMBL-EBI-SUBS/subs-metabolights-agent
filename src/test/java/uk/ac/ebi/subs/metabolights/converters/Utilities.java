package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.metabolights.model.Contact;
import uk.ac.ebi.subs.metabolights.model.StudyContact;
import uk.ac.ebi.subs.metabolights.model.StudyPublication;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kalai on 19/12/2017.
 */
public class Utilities {
    public static StudyContact generateMLContacts(){
        StudyContact contact = new StudyContact();
       contact.setAddress("Hinxton");
       contact.setAffiliation("EMBL-EBI");
       contact.setFax("+11123");
       contact.setEmail("test@abc.com");
       contact.setFirstName("Alice");
       contact.setLastName("Bob");
       return contact;
    }

    public static StudyPublication generateMLPublication(){
        StudyPublication mlPublication = new StudyPublication();
        mlPublication.setPubMedID("1000");
        mlPublication.setAuthorList("Bob, Alice, Tom");
        mlPublication.setTitle("New publication");
        mlPublication.setDoi("123-343-567-890");
        return mlPublication;
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

    public static Team generateTeam (){
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




}
