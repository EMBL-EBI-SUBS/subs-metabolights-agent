package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.io.IOUtils;
import uk.ac.ebi.subs.data.component.*;
import uk.ac.ebi.subs.data.submittable.*;
import uk.ac.ebi.subs.metabolights.model.AssaySpreadSheetConstants;
import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.MetaboLightsTable;
import uk.ac.ebi.subs.metabolights.model.NewMetabolightsAssay;
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
        contact.setFax("11123");
        contact.setPhone("11125");
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

    public static Protocol generateUSIProtocol() {
        Protocol protocol = new Protocol();
        protocol.setTitle("Test chromatography");

        Attribute attribute = new Attribute();
        attribute.setValue("Chromatography");
        protocol.getAttributes().put("protocolType", Arrays.asList(attribute));

        return protocol;
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

    public static List<AssayData> generateUSIAssayDataForSingleAssay() {
        List<AssayData> assayDataList = new ArrayList<>();
        // this id's are mapping to alias in Test_USI_Assay_file.json file
        AssayData assayData1 = new AssayData();
        assayData1.setAlias("a_mtbl2_metabolite profiling_mass spectrometry");
        File file = new File();
        file.setLabel("Acquisition Parameter Data File");
        file.setName("acqus.txt");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Free Induction Decay Data File");
        file.setName("ADG10003u_007.zip");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Derived Spectral Data File");
        file.setName("ADG10003u_007.nmrML");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Metabolite Assignment File");
        file.setName("m_mtbl2_metabolite profiling_mass spectrometry_v2_maf.tsv");
        assayData1.getFiles().add(file);
        assayDataList.add(assayData1);
        return assayDataList;
    }

    public static List<AssayData> generateUSIAssayData() {
        List<AssayData> assayDataList = new ArrayList<>();

        // this id's are mapping to alias in usi_assay_list.json file
        AssayData assayData1 = new AssayData();
        AssayRef assayRef = new AssayRef();
        assayRef.setAlias("nmr_assay_1");
        assayData1.setAssayRefs(Arrays.asList(assayRef));
        assayData1.setAlias("nmr_assay_1_ad");
        File file = new File();
        file.setLabel("Acquisition Parameter Data File");
        file.setName("acqus.txt");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Free Induction Decay Data File");
        file.setName("ADG10003u_007.zip");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Derived Spectral Data File");
        file.setName("ADG10003u_007.nmrML");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Metabolite Assignment File");
        file.setName("m_mtbl2_metabolite profiling_mass spectrometry_v2_maf.tsv");
        assayData1.getFiles().add(file);

        assayDataList.add(assayData1);

        /*
        data 2
         */

        assayData1 = new AssayData();
        assayRef = new AssayRef();
        assayRef.setAlias("nmr_assay_2");
        assayData1.setAssayRefs(Arrays.asList(assayRef));
        assayData1.setAlias("nmr_assay_2_ad");
        file = new File();
        file.setLabel("Acquisition Parameter Data File");
        file.setName("acqus2.txt");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Free Induction Decay Data File");
        file.setName("ADG10003u_007_2.zip");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Derived Spectral Data File");
        file.setName("ADG10003u_007_2.nmrML");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Metabolite Assignment File");
        file.setName("m_mtbl2_metabolite profiling_mass spectrometry_v2_maf_2.tsv");
        assayData1.getFiles().add(file);

        assayDataList.add(assayData1);

         /*
        data 3
         */

        assayData1 = new AssayData();
        assayRef = new AssayRef();
        assayRef.setAlias("nmr_assay_3");
        assayData1.setAssayRefs(Arrays.asList(assayRef));
        assayData1.setAlias("nmr_assay_3_ad");
        file = new File();
        file.setLabel("Acquisition Parameter Data File");
        file.setName("acqus3.txt");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Free Induction Decay Data File");
        file.setName("ADG10003u_007_3.zip");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Derived Spectral Data File");
        file.setName("ADG10003u_007_3.nmrML");
        assayData1.getFiles().add(file);

        file = new File();
        file.setLabel("Metabolite Assignment File");
        file.setName("m_mtbl2_metabolite profiling_mass spectrometry_v2_maf_3.tsv");
        assayData1.getFiles().add(file);

        assayDataList.add(assayData1);
        
        return assayDataList;
    }


    public static uk.ac.ebi.subs.metabolights.model.Study getMLStudyFromDisc() {
        return loadStudy("MTBLS2_isa.json");
    }

    public static Assay getUSIAssayFromDisc() {
        return loadAssay("Test_USI_assay_file.json");
    }

    public static Assay getUSIAssayFromDiscToUpdate() {
        return loadAssay("test_usi_assay_file_to_update.json");
    }

    public static Assay loadAssay(String name) {
        Assay usiAssay = null;
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/" + name));
            usiAssay = mapper.readValue(result, Assay.class);
            return usiAssay;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Study getSimpleUSIStudyFromDisc() {
        return loadUSIStudy("study.json");
    }

    public static uk.ac.ebi.subs.metabolights.model.Study loadStudy(String name) {
        uk.ac.ebi.subs.metabolights.model.Study study = new uk.ac.ebi.subs.metabolights.model.Study();
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/" + name));
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

    public static Study loadUSIStudy(String name) {
        Study study = new Study();
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/" + name));
            try {
                //mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
                study = mapper.readValue(result, Study.class);
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
       return readSampleFromDisc("Test_json/USI_Single_Sample_MTBLS5.json");
    }

    public static uk.ac.ebi.subs.data.submittable.Sample getUSISimpleSampleFromDisc() {

       return readSampleFromDisc("Test_json/USI_Single_Sample.json");
    }

    public static uk.ac.ebi.subs.data.submittable.Sample readSampleFromDisc(String filename){
        uk.ac.ebi.subs.data.submittable.Sample sample;
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream(filename));
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
                samples = mapper.readValue(result, new TypeReference<List<Sample>>() {
                });
                for (uk.ac.ebi.subs.data.submittable.Sample sample : samples) {
                    submittableSamples.add(new uk.ac.ebi.subs.validator.model.Submittable<>(sample, "1"));
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

    public static MetaboLightsTable getTestNmrMetabolightsTableFromDisc() {
        MetaboLightsTable nmrTable;
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/test_ml_assay_table.json"));
            try {
                mapper.registerModule(new JavaTimeModule());
                nmrTable = mapper.readValue(result, MetaboLightsTable.class);
                return nmrTable;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Assay> getUSIAssayListFromDisc() {
        List<uk.ac.ebi.subs.data.submittable.Assay> assays;
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/usi_assay_list.json"));
            try {
                mapper.registerModule(new JavaTimeModule());
                assays = mapper.readValue(result, new TypeReference<List<Assay>>() {
                });
                return assays;
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
        return new Submittable<>(studies.get(0), submissionEnvelope.getSubmission().getId());
    }

    public static Attribute generateSingleUSIAttribute() {
        Attribute usiAttribute_1 = new Attribute();
        usiAttribute_1.setValue("Test_attribute");
        Term term = new Term();
        term.setUrl("http://purl.obolibrary.org/obo/UO_0000036");
        usiAttribute_1.setTerms(Arrays.asList(term));
        usiAttribute_1.setUnits("year");
        return usiAttribute_1;
    }
}
