/*
 * EBI MetaboLights - https://www.ebi.ac.uk/metabolights
 * Metabolomics team
 *
 * European Bioinformatics Institute (EMBL-EBI), European Molecular Biology Laboratory, Wellcome Genome Campus, Hinxton, Cambridge CB10 1SD, United Kingdom
 *
 * Last modified: 2017-Dec-13
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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by kalai on 13/12/2017.
 */
@Data
public class StudyProcessSequence {
    @JsonProperty("@id")
    private String id;
    private List<String> comments;
    private String date;
    private GenericId executesProtocol;
    private List<GenericId> inputs;
    private String name;
    private List<GenericId> outputs;
    private List<String> parameterValues;
    private String performer;


//    private List<CharacteristicCategory> characteristicCategories;
//    private String description;
//    private List<Factor> factors;
//    private String filename;
//    private String identifier;
//    private List<Contact> people;
//    private List<Protocol> protocols;
//    private String publicReleaseDate;
//    private List<Publication> publications;
//    private List<CharacteristicType> studyDesignDescriptors;
//    private String submissionDate;
//    private String title;
}
