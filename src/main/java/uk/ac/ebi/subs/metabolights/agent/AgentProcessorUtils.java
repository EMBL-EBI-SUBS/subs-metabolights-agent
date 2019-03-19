package uk.ac.ebi.subs.metabolights.agent;

import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.ProtocolUse;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.metabolights.model.*;

import java.util.*;

public class AgentProcessorUtils {

    public static boolean containsValue(List entries) {
        return entries != null && entries.size() > 0;
    }

    public static boolean alreadyPresent(List<Factor> factors, String factorAttributeName) {
        for (Factor factor : factors) {
            if (isValid(factor.getFactorName())) {
                if (factor.getFactorName().equalsIgnoreCase(factorAttributeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyPresent(List<Attribute> factorAttributes, Factor factor) {
        for (Attribute attribute : factorAttributes) {
            if (isValid(attribute.getValue())) {
                if (attribute.getValue().equalsIgnoreCase(factor.getFactorName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyPresent(List<Attribute> descriptorAttributes, OntologyModel descriptor) {
        for (Attribute attribute : descriptorAttributes) {
            if (isValid(attribute.getValue())) {
                if (attribute.getValue().equalsIgnoreCase(descriptor.getAnnotationValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<OntologyModel> descriptors, String descriptorAttributeName) {
        for (OntologyModel descriptor : descriptors) {
            if (isValid(descriptor.getAnnotationValue())) {
                if (descriptor.getAnnotationValue().equalsIgnoreCase(descriptorAttributeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<uk.ac.ebi.subs.metabolights.model.Contact> mlContacts, Contact usiContact) {
        for (uk.ac.ebi.subs.metabolights.model.Contact mlContact : mlContacts) {
            if (isValid(mlContact.getEmail())) {
                if (mlContact.getEmail().equalsIgnoreCase(usiContact.getEmail())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<Contact> usiContacts, uk.ac.ebi.subs.metabolights.model.Contact mlContact) {
        for (Contact usiContact : usiContacts) {
            if (isValid(usiContact.getEmail())) {
                if (usiContact.getEmail().equalsIgnoreCase(mlContact.getEmail())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<uk.ac.ebi.subs.metabolights.model.Publication> mlPublications, Publication usiPublication) {
        for (uk.ac.ebi.subs.metabolights.model.Publication mlPublication : mlPublications) {
            if (isValid(mlPublication.getTitle())) {
                if (mlPublication.getTitle().equalsIgnoreCase(usiPublication.getArticleTitle())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<Publication> usiPublications, uk.ac.ebi.subs.metabolights.model.Publication mlPublication) {
        for (Publication publication : usiPublications) {
            if (isValid(publication.getArticleTitle())) {
                if (publication.getArticleTitle().equalsIgnoreCase(mlPublication.getTitle())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<uk.ac.ebi.subs.metabolights.model.Protocol> mlProtocols, Protocol usiProtocol) {
        for (uk.ac.ebi.subs.metabolights.model.Protocol mlProtocol : mlProtocols) {
            if (isValid(mlProtocol.getName())) {
                if (mlProtocol.getName().equalsIgnoreCase(usiProtocol.getTitle())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPresent(Study study, String attributeType) {
        return study.getAttributes().get(attributeType) != null && !study.getAttributes().get(attributeType).isEmpty();
    }

    public static boolean isValid(String object) {
        return object != null && !object.isEmpty();
    }

    public static String getSampleFileName(StudyFiles studyFiles) {
        for (StudyFile studyFile : studyFiles.getStudyFiles()) {
            if (studyFile.getType().equalsIgnoreCase("metadata_sample")) {
                if (studyFile.getStatus().equalsIgnoreCase("active")) {
                    return studyFile.getFile();
                }
            }
        }
        return "";
    }

    public static List<String> getAssayFileName(StudyFiles studyFiles) {
        List<String> assayFileNames = new ArrayList<>();
        for (StudyFile studyFile : studyFiles.getStudyFiles()) {
            if (studyFile.getType().equalsIgnoreCase("metadata_assay")) {
                if (studyFile.getStatus().equalsIgnoreCase("active")) {
                    assayFileNames.add(studyFile.getFile());
                }
            }
        }
        return assayFileNames;
    }

    public static Map<String, List<Sample>> getSamplesToAddAndUpdate(List<Sample> samples, MetaboLightsTable sampleTable) throws Exception {

        List<Sample> samplesToUpdate = new ArrayList<>();
        List<Sample> samplesToAdd = new ArrayList<>();

        if (sampleTable.getData().getRows() != null && sampleTable.getData().getRows().size() > 0) {
            for (Sample sample : samples) {
                if (!sample.getAlias().isEmpty()) {
                    Map<Boolean, String> mappingResult = findMatch(sample.getAlias(), sampleTable);
                    for (Map.Entry<Boolean, String> result : mappingResult.entrySet()) {
                        if (result.getKey().booleanValue()) {
                        /*
                        index to be updated must be set in the samples
                         */
                            Attribute attribute = new Attribute();
                            attribute.setValue(result.getValue());
                            sample.getAttributes().put(SampleSpreadSheetConstants.ROW_INDEX, Arrays.asList(attribute));
                            samplesToUpdate.add(sample);
                        } else {
                            samplesToAdd.add(sample);
                        }
                    }
                }
            }
        }
        Map<String, List<Sample>> seggregatedSamples = new HashMap<>();
        seggregatedSamples.put("add", samplesToAdd);
        seggregatedSamples.put("update", samplesToUpdate);
        return seggregatedSamples;
    }

    public static Map<String, List<uk.ac.ebi.subs.data.submittable.Assay>> getAssayRowsToAddAndUpdate(List<uk.ac.ebi.subs.data.submittable.Assay> assays, MetaboLightsTable assayTable) throws Exception {

        List<uk.ac.ebi.subs.data.submittable.Assay> assayRowsToUpdate = new ArrayList<>();
        List<uk.ac.ebi.subs.data.submittable.Assay> assayRowsToAdd = new ArrayList<>();

        if (assayTable.getData().getRows() != null && assayTable.getData().getRows().size() > 0) {
            for (uk.ac.ebi.subs.data.submittable.Assay assay : assays) {
                if (!assay.getAlias().isEmpty()) {
                    //todo extract id and fid info
                    Map<Boolean, String> mappingResult = hasRowMatch("", "", assayTable);
                    for (Map.Entry<Boolean, String> result : mappingResult.entrySet()) {
                        if (result.getKey().booleanValue()) {
                        /*
                        index to be updated must be set in the assays
                         */
                            Attribute attribute = new Attribute();
                            attribute.setValue(result.getValue());
                            assay.getAttributes().put(AssaySpreadSheetConstants.ROW_INDEX, Arrays.asList(attribute));
                            assayRowsToUpdate.add(assay);
                        } else {
                            assayRowsToAdd.add(assay);
                        }
                    }
                }
            }
        }

        Map<String, List<uk.ac.ebi.subs.data.submittable.Assay>> seggregatedAssayRows = new HashMap<>();
        seggregatedAssayRows.put("add", assayRowsToAdd);
        seggregatedAssayRows.put("update", assayRowsToUpdate);
        return seggregatedAssayRows;
    }

    public static Map<String, String> getUniqueValuesToFilterAssays(uk.ac.ebi.subs.data.submittable.Assay assay) {
        Map<String, String> mappingResult = new HashMap<>();
        String assayID = "";
        String fidDataFileName = "";
        //todo current filtering is for NMR, implement scenarios for other assay types
        if (assay.getProtocolUses() != null && assay.getProtocolUses().size() > 0) {
            for (ProtocolUse protocolUse : assay.getProtocolUses()) {
                if (protocolUse.getProtocolRef().getAlias().equalsIgnoreCase("NMR assay")) {
                    if (protocolUse.getAttributes() != null && protocolUse.getAttributes().size() > 0) {
                        if (protocolUse.getAttributes().containsKey("NMR Assay Name")) {
                            Collection<Attribute> nmr_assay_name = protocolUse.getAttributes().get("NMR Assay Name");
                            Attribute assay_name = nmr_assay_name.iterator().next();
                            assayID = assay_name.getValue();
                        }
                        if (protocolUse.getAttributes().containsKey("Free Induction Decay Data File")) {
                            Collection<Attribute> free_induction_decay_data_file = protocolUse.getAttributes().get("Free Induction Decay Data File");
                            Attribute fid_file_name = free_induction_decay_data_file.iterator().next();
                            fidDataFileName = fid_file_name.getValue();
                        }
                    }
                }
            }
        }
        //todo handle empty protocolUse cases
        mappingResult.put(assayID, fidDataFileName);
        return mappingResult;
    }

    public static List<Integer> getSamplesIndexesToDelete(List<Sample> samples, MetaboLightsTable sampleTable) throws Exception {

        List<Integer> rowsToDelete = new ArrayList<>();

        if (sampleTable.getData().getRows() != null && sampleTable.getData().getRows().size() > 0) {
            if (samples != null && samples.size() > 0) {
                for (Map<String, String> row : sampleTable.getData().getRows()) {
                    boolean isStillPresentInUsiSample = false;
                    for (Map.Entry<String, String> cell : row.entrySet()) {
                        if (cell.getKey().equalsIgnoreCase(SampleSpreadSheetConstants.SAMPLE_NAME)) {
                            for (Sample sample : samples) {
                                if (cell.getValue().equalsIgnoreCase(sample.getAlias())) {
                                    isStillPresentInUsiSample = true;
                                }
                            }
                        }
                    }
                    if (!isStillPresentInUsiSample) {
                        if (row.containsKey(SampleSpreadSheetConstants.ROW_INDEX)) {
                            if (!row.get(SampleSpreadSheetConstants.ROW_INDEX).isEmpty()) {
                                rowsToDelete.add(new Integer(row.get(SampleSpreadSheetConstants.ROW_INDEX)));
                            }
                        }
                    }
                }
            }
        }
        return rowsToDelete;
    }

    public static List<Integer> getAssayRowIndexesToDelete(List<uk.ac.ebi.subs.data.submittable.Assay> assays, MetaboLightsTable assayTable) throws Exception {
        List<Integer> rowsToDelete = new ArrayList<>();
        return rowsToDelete;
    }

    private static Map<Boolean, String> findMatch(String sampleName, MetaboLightsTable sampleTable) {
        Map<Boolean, String> mappingResult = new HashMap<>();
        for (Map<String, String> row : sampleTable.getData().getRows()) {
            for (Map.Entry<String, String> cell : row.entrySet()) {
                if (cell.getKey().equalsIgnoreCase(SampleSpreadSheetConstants.SAMPLE_NAME)) {
                    if (cell.getValue().equalsIgnoreCase(sampleName)) {
                        mappingResult.put(Boolean.TRUE, row.get(SampleSpreadSheetConstants.ROW_INDEX));
                        return mappingResult;
                    }
                }
            }
        }
        mappingResult.put(Boolean.FALSE, "");
        return mappingResult;
    }

    private static Map<Boolean, String> hasRowMatch(String assayID, String fidDataFile, MetaboLightsTable assayTable) {
        Map<Boolean, String> mappingResult = new HashMap<>();
        for (Map<String, String> row : assayTable.getData().getRows()) {
            boolean assayIdMatch = false;
            boolean fidFileMatch = false;
            for (Map.Entry<String, String> cell : row.entrySet()) {
                if (cell.getKey().equalsIgnoreCase(assayID)) {
                    assayIdMatch = true;
                }
                if (cell.getKey().equalsIgnoreCase(fidDataFile)) {
                    fidFileMatch = true;
                }
            }
            if (assayIdMatch && fidFileMatch) {
                mappingResult.put(Boolean.TRUE, row.get(AssaySpreadSheetConstants.ROW_INDEX));
                return mappingResult;
            }
        }
        mappingResult.put(Boolean.FALSE, "");
        return mappingResult;
    }

    public static Map<String, List<uk.ac.ebi.subs.data.submittable.Assay>> getAssaysToAddAndUpdate(List<uk.ac.ebi.subs.data.submittable.Assay> assays, MetaboLightsTable assayTable) throws Exception {

        List<uk.ac.ebi.subs.data.submittable.Assay> assaysToUpdate = new ArrayList<>();
        List<uk.ac.ebi.subs.data.submittable.Assay> assaysToAdd = new ArrayList<>();

        if (assayTable.getData().getRows() != null && assayTable.getData().getRows().size() > 0) {
            for (uk.ac.ebi.subs.data.submittable.Assay assay : assays) {
                if (!assay.getAlias().isEmpty()) {
                    Map<Boolean, String> mappingResult = findMatch(assay.getAlias(), assayTable);
                    for (Map.Entry<Boolean, String> result : mappingResult.entrySet()) {
                        if (result.getKey().booleanValue()) {
                        /*
                        index to be updated must be set in the samples
                         */
                            Attribute attribute = new Attribute();
                            attribute.setValue(result.getValue());
                            assay.getAttributes().put("index", Arrays.asList(attribute));
                            assaysToUpdate.add(assay);
                        } else {
                            assaysToAdd.add(assay);
                        }
                    }
                }
            }
        }
        Map<String, List<uk.ac.ebi.subs.data.submittable.Assay>> seggregatedSamples = new HashMap<>();
        seggregatedSamples.put("add", assaysToAdd);
        seggregatedSamples.put("update", assaysToUpdate);
        return seggregatedSamples;
    }

    public static String getTechnologyType(uk.ac.ebi.subs.data.submittable.Assay assay) {
        if (assay.getAttributes() != null && assay.getAttributes().size() > 0) {
            Map<String, Collection<Attribute>> attributes = assay.getAttributes();
            if (attributes.containsKey("technologyType")) {
                Collection<Attribute> technologyType = attributes.get("technologyType");
                if (!technologyType.isEmpty()) {
                    return technologyType.iterator().next().getValue();
                }
            }
        }
        return "";
    }


    public static NewMetabolightsAssay generateNewNMRAssay() {
        NewMetabolightsAssay nmrMetabolightsAssay = new NewMetabolightsAssay();
        nmrMetabolightsAssay.setType("NMR");
        nmrMetabolightsAssay.setColumns(new ArrayList<>());
        return nmrMetabolightsAssay;
    }
}
