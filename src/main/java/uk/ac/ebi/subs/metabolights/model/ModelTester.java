/*
 * EBI MetaboLights - https://www.ebi.ac.uk/metabolights
 * Metabolomics team
 *
 * European Bioinformatics Institute (EMBL-EBI), European Molecular Biology Laboratory, Wellcome Genome Campus, Hinxton, Cambridge CB10 1SD, United Kingdom
 *
 * Last modified: 2017-Dec-12
 * Modified by:   kalai
 *
 * Copyright 2017 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.ac.ebi.subs.metabolights.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kalai on 12/12/2017.
 */
public class ModelTester {

    //todo Client to call python API and map it into model objects.
    //todo export JSON that is compatible with ISA python API model

    public static void main(String[] args) {
        ModelTester tester = new ModelTester();
        tester.processProject();
    }

    private void processContacts(){
        String response = sendGetRequest("contacts");
        Contacts contacts = parseJson(response,Contacts.class);
        System.out.println(contacts.toString());
        for(Contact contact :contacts.getStudyContacts()){
            System.out.println(contact.getFirst_name());
            System.out.println(contact.getLast_name());
        }
//        System.out.println("----------");
//        System.out.println(parseToJSONString(contacts));
    }

    private void processPublications(){
        String response = sendGetRequest("publications");
        Publications publications = parseJson(response,Publications.class);
        System.out.println(publications.toString());
        for(Publication publication : publications.getPublications()){
            System.out.println(publication.getTitle());
            System.out.println(publication.getDoi());
        }
    }
    private void processFactors(){
        String response = sendGetRequest("factors");
        Factors factors = parseJson(response,Factors.class);
        System.out.println(factors.toString());
        for(Factor factor : factors.getFactors()){
            System.out.println(factor.getFactor_type().getTerm_accession());
            System.out.println(factor.getName());
        }
    }

    private void processDescriptors(){
        String response = sendGetRequest("descriptors");
        DesignDescriptors designDescriptors = parseJson(response,DesignDescriptors.class);
        System.out.println(designDescriptors.toString());
        for(OntologyModel ontologyModel : designDescriptors.getDescriptors()){
            System.out.println(ontologyModel.getTerm_accession());
            System.out.println(ontologyModel.getTerm());
        }
    }


    private void processSamples(){
        String response = sendGetRequest("samples");
        Samples samples = parseJson(response,Samples.class);
        for (int i = 0; i < samples.getSampleNames().size(); i++) {
            System.out.println(samples.getSampleNames().get(i));
        }

    }

    private void processProtocols(){
        String response = sendGetRequest("protocols");
        Protocols protocols = parseJson(response,Protocols.class);
        for (int i = 0; i < protocols.getProtocols().size(); i++) {
            System.out.println(protocols.getProtocols().get(i).getDescription());
        }
    }

    private void processProject(){
        String response = sendGetRequest("isa_json");
        Project project = parseJson(response,Project.class);
        System.out.println("Project ID = " +  project.getIdentifier());
        Study study = project.getStudies().get(0);
        System.out.println("Study title = " + study.getTitle());

        //protocols
        System.out.println("Protocols");
        for(StudyProtocol protocol : study.getProtocols()){
            System.out.println(protocol.getDescription());
        }

        //Design factors
        System.out.println("SDD");
        for(CharacteristicType type : study.getStudyDesignDescriptors()){
            System.out.println(type.getAnnotationValue());
        }

        //Assays
        System.out.println("Assays");
        for(Assay assay : study.getAssays()){
            System.out.println(assay.getFilename());
        }

        //Contacts
        System.out.println("Contacts");
        for(StudyContact studyContact : study.getPeople()){
            System.out.println(studyContact.getFirstName() + " - " + studyContact.getLastName());
        }

        //publications
        System.out.println("Publications");
        for(StudyPublication publication : study.getPublications()){
            System.out.println(publication.getDoi());
        }

        //samples
        System.out.println("Samples");
        for(StudySample sample : study.getMaterials().getSamples()){
            System.out.println(sample.getName());
        }
    }

    private String sendGetRequest(String endpoint){
        try {

            URL url = new URL("http://ves-ebi-8d.ebi.ac.uk:5000/mtbls/ws/study/MTBLS10/" + endpoint);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String message = org.apache.commons.io.IOUtils.toString(br);

            conn.disconnect();
            System.out.println(message);
            System.out.println("---------------------------------");
            return message;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T parseJson(String response, Class<T> valueType ){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(response, valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseToJSONString(Object toConvert) {
        if(toConvert == null) return "";
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(toConvert);
        } catch (IOException e) {
            return "";
        }

    }
}
