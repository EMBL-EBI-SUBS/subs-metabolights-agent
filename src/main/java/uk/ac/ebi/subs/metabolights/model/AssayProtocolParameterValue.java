package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.List;

@Data
public class AssayProtocolParameterValue {
    private OntologyModel unit;
    private ProtocolParameter category;
    private Object value;
}
