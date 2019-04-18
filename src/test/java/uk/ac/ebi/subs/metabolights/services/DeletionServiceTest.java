package uk.ac.ebi.subs.metabolights.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.converters.*;
import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@SpringBootTest(classes = {
        PostService.class, DeletionService.class})
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class DeletionServiceTest {

    @Autowired
    private DeletionService deletionService;
    @Autowired
    private PostService postService;


    public void deletePublication() {
        Publication publication = Utilities.generateUSIPublication();
        String newTitle = publication.getArticleTitle() + " - " + UUID.randomUUID().toString();
        publication.setArticleTitle(newTitle);
        uk.ac.ebi.subs.metabolights.model.Publication modified = this.postService.add("MTBLS2", publication);
        System.out.println(newTitle);

        MLPublicationToUSIPublication converter = new MLPublicationToUSIPublication();
        this.deletionService.deletePublication("MTBLS2", modified.getTitle());
        System.out.println("deleted - " + newTitle);
        //TODO assert method
    }


    public void deleteProtocol() {
        Protocol protocol = Utilities.generateUSIProtocol();
        String newTitle = protocol.getTitle() + " - " + UUID.randomUUID().toString();
        protocol.setTitle(newTitle);
        uk.ac.ebi.subs.metabolights.model.Protocol modified = this.postService.add("MTBLS2", protocol);
        System.out.println(newTitle);

        MLProtocolToUSIProtocol converter = new MLProtocolToUSIProtocol();
        this.deletionService.deleteProtocol("MTBLS2", converter.convert(modified));
        System.out.println("deleted - " + newTitle);
        //TODO assert method
    }


    public void deleteFactor() {
        Attribute attribute = Utilities.generateSingleUSIAttribute();
        String newName = attribute.getValue() + " - " + UUID.randomUUID().toString();
        attribute.setValue(newName);
        Factor factor = this.postService.addFactor("MTBLS2", attribute);
        System.out.println(newName);

        MLFactorToUSIFactor mlFactorToUSIFactor = new MLFactorToUSIFactor();
        this.deletionService.deleteFactor("MTBLS2", mlFactorToUSIFactor.convert(factor).getValue());
        System.out.println("deleted - " + newName);
        //TODO assert method
    }


    public void deleteDescriptor() {
        Attribute attribute = Utilities.generateSingleUSIAttribute();
        String newName = attribute.getValue() + " - " + UUID.randomUUID().toString();
        attribute.setValue(newName);
        OntologyModel descriptor = this.postService.addDescriptor("MTBLS2", attribute);
        System.out.println(newName);

        MLDescriptorToUSIDescriptor mlDescriptorToUSIDescriptor = new MLDescriptorToUSIDescriptor();
        this.deletionService.deleteDescriptor("MTBLS2", mlDescriptorToUSIDescriptor.convert(descriptor).getValue());
        System.out.println("deleted - " + newName);
        //TODO assert method
    }
    
    public void deleteAssayRow() {
        String studyID = "MTBLS_DEV2346";
        String filename = "a_MTBLS_DEV2346_NMR___metabolite_profiling.txt";
        List<Integer> rowsToDelete = new ArrayList();
        rowsToDelete.add(2);
        this.deletionService.deleteTableRows(studyID, filename, rowsToDelete);
    }
    
    public void deleteBioStudiesID() {
        String studyID = "MTBLS_DEV2344";
        String biostudiesAcc = UUID.randomUUID().toString();
        String addedAccession = this.postService.addBioStudiesAccession(studyID, biostudiesAcc);
        this.deletionService.deleteBioStudiesID(studyID);
    }
}