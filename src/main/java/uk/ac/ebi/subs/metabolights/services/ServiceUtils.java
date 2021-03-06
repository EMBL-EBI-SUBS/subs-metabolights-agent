package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.metabolights.model.Header;
import uk.ac.ebi.subs.metabolights.model.SampleMap;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServiceUtils {

    static final ObjectMapper objectMapper = new ObjectMapper();


    public static JSONObject convertToJSONObject(List objectList, String name) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JsonNode listNode = objectMapper.valueToTree(objectList);
        org.json.JSONArray request = new org.json.JSONArray(listNode.toString());
        jsonObject.put(name, request);
        return jsonObject;
    }

    public static ObjectNode convertToJSON(Object object, String name) {
        ObjectNode root = objectMapper.createObjectNode();
        JsonNode jsonNode = objectMapper.valueToTree(object);
        root.set(name, jsonNode);
        return root;
    }

    public static String getAsConcatenatedString(List<Integer> sampleRowsToDelete) {
        if (sampleRowsToDelete.size() == 1) {
            return sampleRowsToDelete.get(0).toString();
        } else {
            String concatenatedRows = "";
            for (int i = 0; i < sampleRowsToDelete.size(); i++) {
                if (i != sampleRowsToDelete.size() - 1) {
                    concatenatedRows += sampleRowsToDelete.get(i) + ",";
                } else {
                    concatenatedRows += sampleRowsToDelete.get(i);
                }
            }
            return concatenatedRows;
        }
    }

    public static void fillEmptyValuesForMissingColumns(LinkedHashMap<String, String> map, Map<String, Header> existingSampleTableHeaders) {
        for (Map.Entry<String, Header> entry : existingSampleTableHeaders.entrySet()) {
            if (!map.containsKey(entry.getKey())) {
                map.put(entry.getKey(), "");
            }
        }
    }

    public static void fillEmptyValuesForMissingColumnsForSamples(LinkedHashMap<String, String> map, Map<String, String> existingSampleTableHeaders) {
        for (Map.Entry<String, String> entry : existingSampleTableHeaders.entrySet()) {
            if (!map.containsKey(entry.getKey())) {
                map.put(entry.getKey(), "");
            }
        }
    }

    public static String getMLstudyId(Study usiStudy) {
        Collection<Attribute> mlStudyIDs = usiStudy.getAttributes().get("mlStudyID");
        return mlStudyIDs.iterator().next().getValue();
    }
}
