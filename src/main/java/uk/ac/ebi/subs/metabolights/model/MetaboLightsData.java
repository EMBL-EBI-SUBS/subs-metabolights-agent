package uk.ac.ebi.subs.metabolights.model;

import java.util.List;
import java.util.Map;

@lombok.Data
public class Data {
    private List<Map<String, String>> rows;
}
