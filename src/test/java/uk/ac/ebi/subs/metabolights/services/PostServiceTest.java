package uk.ac.ebi.subs.metabolights.services;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Assay;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.metabolights.agent.AgentProcessorUtils;
import uk.ac.ebi.subs.metabolights.converters.USISampleToMLSample;
import uk.ac.ebi.subs.metabolights.converters.Utilities;
import uk.ac.ebi.subs.metabolights.model.MetaboLightsTable;
import uk.ac.ebi.subs.metabolights.model.MetaboLightsTableResult;
import uk.ac.ebi.subs.metabolights.model.NewMetabolightsAssay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@SpringBootTest(classes = {
        PostService.class, FetchService.class})
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private FetchService fetchService;

    @Test
    public void addContacts() {
        Contact contact = Utilities.generateUSIContact();
        contact.setEmail(UUID.randomUUID().toString() + "@dummy.com");
        System.out.println(contact);
        List<uk.ac.ebi.subs.metabolights.model.Contact> addedContacts = this.postService.addContacts("MTBLS_DEV2380", Arrays.asList(contact));
        boolean added = false;
        for (uk.ac.ebi.subs.metabolights.model.Contact addedContact : addedContacts) {
            if (addedContact.getEmail().equalsIgnoreCase(contact.getEmail())) {
                added = true;
            }
        }
//        assertEquals(contact.getEmail(), addedContacts.get(0).getEmail());
        assertTrue(added);
    }

    @Test
    public void addPublication() {
        Publication publication = Utilities.generateUSIPublication();
        String newTitle = publication.getArticleTitle() + " - " + UUID.randomUUID().toString();
        publication.setArticleTitle(newTitle);
        uk.ac.ebi.subs.metabolights.model.Publication addedPublication = this.postService.add("MTBLS2", publication);
        assertEquals(addedPublication.getTitle(), newTitle);
    }

    @Test
    public void addProtocol() {
        Protocol protocol = Utilities.generateUSIProtocol();
        String newTitle = protocol.getTitle() + " - " + UUID.randomUUID().toString();
        protocol.setTitle(newTitle);
        uk.ac.ebi.subs.metabolights.model.Protocol addedProtocol = this.postService.add("MTBLS2", protocol);
        assertEquals(addedProtocol.getName(), newTitle);
    }

    @Test
    public void addNewAssay() {
        NewMetabolightsAssay newMetabolightsAssay = AgentProcessorUtils.generateNewNMRAssay();
        HttpStatus status = this.postService.addNewAssay(newMetabolightsAssay, "MTBLS_DEV2346");
        assertEquals(status.is2xxSuccessful(), true);
    }

    @Test
    public void addNewAssayRows() {

        String studyID = "MTBLS_DEV2346";
        String assayFileName = "a_MTBLS_DEV2346_NMR___metabolite_profiling.txt";
        MetaboLightsTable assayTable = this.fetchService.getMetaboLightsDataTable(studyID, assayFileName);
        Assay usiAssay = Utilities.getUSIAssayFromDisc();
        List<Assay> assayList = new ArrayList();
        assayList.add(usiAssay);

        this.postService.addAssayRows(assayList, studyID, assayFileName, assayTable.getHeader());
    }

    @Test
    public void addNewSampleRows() {

        String studyID = "MTBLS_DEV2380";
        String sampleFileName = "s_MTBLS_DEV2380.txt";
        MetaboLightsTableResult sampleTable = this.fetchService.getMetaboLightsSampleDataTable(studyID, sampleFileName);
        Sample sample = Utilities.getUSISampleFromDisc();
        List<Sample> sampleList = new ArrayList();
        sampleList.add(sample);

        this.postService.addSampleRows(sampleList, studyID, sampleFileName, sampleTable.getHeader());
    }

    @Test
    public void addNewBiostudiesAccession() {
        String studyID = "MTBLS_DEV2346";
        String biostudiesAcc = UUID.randomUUID().toString();
        String addedAccession = this.postService.addBioStudiesAccession(studyID, biostudiesAcc);
        assertEquals(addedAccession, biostudiesAcc);
    }
}