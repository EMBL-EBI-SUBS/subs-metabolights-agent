package uk.ac.ebi.subs.metabolights.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ServiceUtils {

    static final ObjectMapper objectMapper = new ObjectMapper();


    public static JSONObject convertToJSON(List objectList, String name) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JsonNode listNode = objectMapper.valueToTree(objectList);
        org.json.JSONArray request = new org.json.JSONArray(listNode.toString());
        jsonObject.put(name, request);
        return jsonObject;
    }

    public static ObjectNode convertToJSON(Object object, String name){
        ObjectNode root = objectMapper.createObjectNode();
        JsonNode jsonNode = objectMapper.valueToTree(object);
        root.set(name, jsonNode);
        return root;
    }
}
