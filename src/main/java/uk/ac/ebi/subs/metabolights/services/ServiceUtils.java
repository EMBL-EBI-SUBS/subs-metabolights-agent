package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ServiceUtils {

    public static JSONObject convertToJSON(List objectList, String name) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode listNode = objectMapper.valueToTree(objectList);
        org.json.JSONArray request = new org.json.JSONArray(listNode.toString());
        jsonObject.put(name , request);
        return jsonObject;
    }
}
