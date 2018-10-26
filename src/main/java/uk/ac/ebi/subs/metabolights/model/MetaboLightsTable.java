package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MetaboLightsTable {

    private Data data;
    private Map<String, String> header;

    @lombok.Data
    private class Data {
        private List<Map<String, String>> rows;
    }
//
//    @lombok.Data
//    private class Entry extends HashMap<String, String> {
//
//    }
}
