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

import lombok.Data;

import java.util.List;

/**
 * Created by kalai on 13/12/2017.
 */
@Data
public class AssayProcessSequence {
    private List<Comment> comments;
    private String name;
    private Protocol executesProtocol;
    private String date;
    private String  performer;
    private List<AssayProtocolParameterValue> parameterValues;
    private List<Object> inputs;
    private List<Object> outputs;

//    private GenericId nextProcess;
//    private GenericId previousProcess;
    //todo harmonise assay and study process seqeuence
}


