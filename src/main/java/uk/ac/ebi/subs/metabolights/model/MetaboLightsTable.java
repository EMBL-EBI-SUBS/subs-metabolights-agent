package uk.ac.ebi.subs.metabolights.model;


import java.util.Map;

@lombok.Data
public class MetaboLightsTable {

    private MetaboLightsData data;
    private Map<String, Header> header;
}
