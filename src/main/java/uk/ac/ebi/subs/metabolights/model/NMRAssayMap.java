package uk.ac.ebi.subs.metabolights.model;

import java.util.LinkedHashMap;

/**
 * Created by kalai on 22/10/2018.
 */

public class NMRAssayMap extends LinkedHashMap<String, String> {

    public NMRAssayMap(Assay assay) {
        super();
        process(assay);
    }

    private void process(Assay assay) {
        put(AssaySpreadSheetConstants.SAMPLE_NAME, "");

        put(AssaySpreadSheetConstants.EXTRACTION_PROTOCOL_REF,"");
        put(AssaySpreadSheetConstants.EXTRACTION_PROTOCOL_EXTRACTION_METHOD,"");
        put(AssaySpreadSheetConstants.EXTRACTION_PROTOCOL_EXTRACT_NAME,"");

        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_REF,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TUBE_TYPE ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TUBE_TYPE_TSR ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TUBE_TYPE_TAN ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_SOLVENT ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_SOLVENT_TSR ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_SOLVENT_TAN ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_SAMPLE_PH ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TEMPERATURE ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_UNIT,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TEMPERATURE_UNIT_TSR ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TEMPERATURE_UNIT_TAN ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_EXTRACT_NAME ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_LABEL,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_EXTRACT_NAME_TSR ,"");
        put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_EXTRACT_NAME_TAN ,"");

        put(AssaySpreadSheetConstants.NMR_PROTOCOL_REF ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_INSTRUMENT ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_INSTRUMENT_TSR ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_INSTRUMENT_TAN ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_NMR_PROBE ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_NMR_PROBE_TSR ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_NMR_PROBE_TAN ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_NO_OF_TRANSIENTS ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_PULSE_SEQ_NAME ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_MAGNETIC_FIELD_STRENGTH ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_UNIT ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_UNIT_TSR ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_UNIT_TAN ,"");
        put(AssaySpreadSheetConstants.NMR_PROTOCOL_ACQUISITION_PM_DATA_FILE ,"");

        



    }
}
