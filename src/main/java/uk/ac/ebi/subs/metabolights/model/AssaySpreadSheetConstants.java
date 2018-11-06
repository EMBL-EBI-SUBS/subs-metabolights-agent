package uk.ac.ebi.subs.metabolights.model;

/**
 * Created by kalai on 06/11/2018.
 */

public class AssaySpreadSheetConstants {

    public static final String SAMPLE_NAME = "Sample Name";


    /*
     Extraction protocol common for NMR and MS
      */
    public static final String EXTRACTION_PROTOCOL_REF = "Protocol REF";
    //Extraction Protocol column specific for MS
    public static final String EXTRACTION_PROTOCOL_POST_EXTRACTION = "Parameter Value[Post Extraction]";
    public static final String EXTRACTION_PROTOCOL_DERIVATIZATION = "Parameter Value[Derivatization]";
    //Extraction Protocol column specific for NMR
    public static final String EXTRACTION_PROTOCOL_EXTRACTION_METHOD = "Parameter Value[Extraction Method]";
    //Extraction Protocol column common for NMR and MS
    public static final String EXTRACTION_PROTOCOL_EXTRACT_NAME = "Extract Name";

    /*
     Chromatography protocol specific for MS
      */

    public static final String CHROMATOGRAPHY_PROTOCOL_REF = "Protocol REF.1";
    public static final String CHROMATOGRAPHY_PROTOCOL_INSTRUMENT = "Parameter Value[Chromatography Instrument]";
    public static final String CHROMATOGRAPHY_PROTOCOL_INSTRUMENT_TSR = "Term Source REF";
    public static final String CHROMATOGRAPHY_PROTOCOL_INSTRUMENT_TAN = "Term Accession Number";
    public static final String CHROMATOGRAPHY_PROTOCOL_COLUMN_MODEL = "Parameter Value[Column model]";
    public static final String CHROMATOGRAPHY_PROTOCOL_COLUMN_TYPE = "Parameter Value[Column type]";
    public static final String CHROMATOGRAPHY_PROTOCOL_EXTRACT_NAME = "Labeled Extract Name";
    public static final String CHROMATOGRAPHY_PROTOCOL_LABEL = "Label";
    public static final String CHROMATOGRAPHY_PROTOCOL_EXTRACT_NAME_TSR = "Term Source REF.1";
    public static final String CHROMATOGRAPHY_PROTOCOL_EXTRACT_NAME_TAN = "Term Accession Number.1";

     /*
     NMR sample protocol specific for NMR
      */

    public static final String NMR_SAMPLE_PROTOCOL_REF = "Protocol REF.1";
    public static final String NMR_SAMPLE_PROTOCOL_TUBE_TYPE = "Parameter Value[NMR tube type]";
    public static final String NMR_SAMPLE_PROTOCOL_TUBE_TYPE_TSR = "Term Source REF";
    public static final String NMR_SAMPLE_PROTOCOL_TUBE_TYPE_TAN = "Term Accession Number";
    public static final String NMR_SAMPLE_PROTOCOL_SOLVENT = "Parameter Value[Solvent]";
    public static final String NMR_SAMPLE_PROTOCOL_SOLVENT_TSR = "Term Source REF.1";
    public static final String NMR_SAMPLE_PROTOCOL_SOLVENT_TAN = "Term Accession Number.1";
    public static final String NMR_SAMPLE_PROTOCOL_SAMPLE_PH = "Parameter Value[Sample pH]";
    public static final String NMR_SAMPLE_PROTOCOL_TEMPERATURE = "Parameter Value[Temperature]";
    public static final String NMR_SAMPLE_PROTOCOL_UNIT = "Unit";
    public static final String NMR_SAMPLE_PROTOCOL_TEMPERATURE_UNIT_TSR = "Term Source REF.2";
    public static final String NMR_SAMPLE_PROTOCOL_TEMPERATURE_UNIT_TAN = "Term Accession Number.2";
    public static final String NMR_SAMPLE_PROTOCOL_EXTRACT_NAME = "Labeled Extract Name";
    public static final String NMR_SAMPLE_PROTOCOL_LABEL = "Label";
    public static final String NMR_SAMPLE_PROTOCOL_EXTRACT_NAME_TSR = "Term Source REF.3";
    public static final String NMR_SAMPLE_PROTOCOL_EXTRACT_NAME_TAN = "Term Accession Number.3";
    

}
