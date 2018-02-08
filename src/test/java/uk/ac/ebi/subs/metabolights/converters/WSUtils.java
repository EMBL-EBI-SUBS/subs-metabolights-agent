package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import uk.ac.ebi.subs.metabolights.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kalai on 31/01/2018.
 */
public class WSUtils {

    private static final String metabolightsWsUrl = "http://ves-ebi-90:5000/mtbls/ws/study/";

    public static String makeGetRequest(String path, Object dataToSend, String method) {

        try {
            // Get a post connection
            HttpURLConnection conn = getHttpURLConnection(path, method);

            conn.setRequestProperty("content-type", "application/json");
            conn.setDoOutput(true);

            if (dataToSend != null) {

                String json = null;

                // If it's not a String
                if (!(dataToSend instanceof String)) {

                    json = serializeObject(dataToSend);
                } else {
                    json = (String) dataToSend;
                }

                // Send JSON content
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());

                out.write(json);
                out.close();

            }

            // Read response
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("MetaboLights Java WS client: " + conn.getURL().toString() + "(" + method + ") request failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String message = org.apache.commons.io.IOUtils.toString(br);

            conn.disconnect();
            return message;

        } catch (MalformedURLException e) {
            System.out.println("Malformed url: " + path);
            return null;
        } catch (IOException e) {
            System.out.println("IO exception while trying to reach " + path);
            return null;
        }
    }

    public static HttpURLConnection getHttpURLConnection(String path, String method) throws IOException {

        URL url = new URL(metabolightsWsUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Accept", "application/json");

        return conn;
    }

    public static String serializeObject(Object objectToSerialize) {
        // Get the mapper
        ObjectMapper mapper = new ObjectMapper();
        try {

            return mapper.writeValueAsString(objectToSerialize);

        } catch (IOException e) {

            System.out.println("Can't serialize " + objectToSerialize.getClass());
        }
        return null;
    }

    public static Contacts getMLContacts(String mtblsID) {
        String response = makeGetRequest(mtblsID + "/contacts", null, "GET");
        ObjectMapper mapper = new ObjectMapper();
        Contacts contacts = new Contacts();
        try {
            contacts = mapper.readValue(response, Contacts.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public static Sample getTestMLSample() {
        String response = makeGetRequest("MTBLS2/samples/Ex1-Col0-48h-Ag-1", null, "GET");
        System.out.println(response);
        ObjectMapper mapper = new ObjectMapper();
        Sample sample = new Sample();
        try {
            JSONObject myObject = new JSONObject(response);
            String mlSample = myObject.getString("Study_sample");
            sample = mapper.readValue( mlSample
                   , Sample.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sample;
    }

    public static Study getMLStudy(String mtblsID) {
        String response = makeGetRequest(mtblsID + "/isa_json", null, "GET");
        ObjectMapper mapper = new ObjectMapper();
        Study study = new Study();
        Project project = new Project();
        try {
            project = mapper.readValue(response, Project.class);
            study = project.getStudies().get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return study;
    }
}
