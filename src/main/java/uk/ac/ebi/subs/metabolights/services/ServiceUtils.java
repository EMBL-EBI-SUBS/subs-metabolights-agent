package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.ebi.subs.metabolights.model.SampleMap;

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

    public static void fillEmptyValuesForMissingColumns(SampleMap sampleMap, Map<String, String> existingSampleTableHeaders) {
        for (Map.Entry<String, String> entry : existingSampleTableHeaders.entrySet()) {
            if (!sampleMap.containsKey(entry.getKey())) {
                sampleMap.put(entry.getKey(), "");
            }
        }
    }
}
