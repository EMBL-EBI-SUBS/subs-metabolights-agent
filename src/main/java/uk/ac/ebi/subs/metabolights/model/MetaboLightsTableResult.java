package uk.ac.ebi.subs.metabolights.model;


import java.util.Map;

@lombok.Data
public class MetaboLightsTableResult {

    private MetaboLightsData data;
    private Map<String, String> header;
}
