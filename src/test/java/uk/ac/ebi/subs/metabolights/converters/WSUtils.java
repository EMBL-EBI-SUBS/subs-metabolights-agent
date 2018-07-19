package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import uk.ac.ebi.subs.metabolights.model.*;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalai on 31/01/2018.
 */
public class WSUtils {

    private static final String metabolightsWsUrl = "http://ves-ebi-8d:5000/mtbls/ws/studies/";
    public static final ObjectMapper mapper = new ObjectMapper();

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
        try {

            return mapper.writeValueAsString(objectToSerialize);

        } catch (IOException e) {

            System.out.println("Can't serialize " + objectToSerialize.getClass());
        }
        return null;
    }

    public static List<Contact> getMLContacts(String mtblsID) {
        Study study = getMLStudy(mtblsID);
        List<Contact> contacts = study.getPeople();
        return contacts;
    }

    public static Sample getTestMLSample() {
        String response = makeGetRequest("MTBLS3/samples?name=Cecilia_AA_batch23_05&list_only=true", null, "GET");
        System.out.println(response);
        return processSampleResponse(response);
    }

    public static Sample processSampleResponse(String response) {
        Sample sample = new Sample();
        try {
            JSONObject myObject = new JSONObject(response);
            String mlSample = myObject.getString("Study_sample");
            sample = mapper.readValue(mlSample
                    , Sample.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sample;
    }

    public static List<Protocol> getMLStudyProtocols(String mtblsID) {
        Study study = getMLStudy(mtblsID);
        List<Protocol> protocols = study.getProtocols();
        return protocols;
    }

    public static List<Sample> getMLStudySamples(String mtblsID) {
        Study study = getMLStudy(mtblsID);
        List<Sample> mlSamples = study.getSamples();
        return mlSamples;
    }

    public static Study getMLStudy(String mtblsID) {
        String response = makeGetRequest(mtblsID, null, "GET");
        Study study = new Study();
        Project project = new Project();
        try {
            mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
            project = mapper.readValue(response, Project.class);
            study = project.getStudies().get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return study;
    }

    public static Study getMLStudyFromDisc() {
        Study study = new Study();
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/MTBLS2_isa.json"));
            Project project = new Project();
            try {
                mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
                project = mapper.readValue(result, Project.class);
                study = project.getStudies().get(0);
                return study;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return study;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Project getMLProjectFromDisc() {
        Project project = new Project();
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/MTBLS2_isa.json"));
            try {
                mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
                project = mapper.readValue(result, Project.class);
                return project;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SubmissionEnvelope getUSISubmisisonFromDisc() {
        SubmissionEnvelope submissionEnvelope = new SubmissionEnvelope();
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/MTBLS2_usi_2.json"));
            try {
                mapper.registerModule(new JavaTimeModule());
                submissionEnvelope = mapper.readValue(result, SubmissionEnvelope.class);
                return submissionEnvelope;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Sample getMLSampleFromDisc() {
        Sample sample;
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/ML_Single_Sample.json"));
            try {
                mapper.registerModule(new JavaTimeModule());
                sample = mapper.readValue(result, Sample.class);
                return sample;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static uk.ac.ebi.subs.data.submittable.Sample getUSISampleFromDisc() {
        uk.ac.ebi.subs.data.submittable.Sample sample;
        try {
            String result = IOUtils.toString(WSUtils.class.getClassLoader().getResourceAsStream("Test_json/USI_Single_Sample.json"));
            try {
                mapper.registerModule(new JavaTimeModule());
                sample = mapper.readValue(result, uk.ac.ebi.subs.data.submittable.Sample.class);
                return sample;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
