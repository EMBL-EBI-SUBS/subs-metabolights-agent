package uk.ac.ebi.subs.metabolights.agent;

import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Contact;
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

    public static String getFileName(StudyFiles studyFiles, String prefix) {
        for (StudyFile studyFile : studyFiles.getStudyFiles()) {
            if (studyFile.getFile().toLowerCase().startsWith(prefix)) {
                return studyFile.getFile();
            }
        }
        return "";
    }

    public static String getSampleFileName(StudyFiles studyFiles) {
        return getFileName(studyFiles, "s_");
    }

    public static String getAssayFileName(StudyFiles studyFiles) {
        //todo some cases have multiple a_ files
        return getFileName(studyFiles, "a_");
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


}
