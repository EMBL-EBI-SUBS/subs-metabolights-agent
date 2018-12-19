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

    /*
     Mass Spectrometry protocol specific for MS
      */

    public static final String MS_PROTOCOL_REF = "Protocol REF.2";
    public static final String MS_PROTOCOL_SCAN_POLARITY = "Parameter Value[Scan polarity]";
    public static final String MS_PROTOCOL_SCAN_MZ_RANGE = "Parameter Value[Scan m/z range]";
    public static final String MS_PROTOCOL_INSTRUMENT = "Parameter Value[Instrument]";
    public static final String MS_PROTOCOL_INSTRUMENT_TSR = "Term Source REF.2";
    public static final String MS_PROTOCOL_INSTRUMENT_TAN = "Term Accession Number.2";
    public static final String MS_PROTOCOL_ION_SOURCE = "Parameter Value[Ion source]";
    public static final String MS_PROTOCOL_ION_SOURCE_TSR = "Term Source REF.3";
    public static final String MS_PROTOCOL_ION_SOURCE_TAN = "Term Accession Number.3";
    public static final String MS_PROTOCOL_MASS_ANALYZER = "Parameter Value[Mass analyzer]";
    public static final String MS_PROTOCOL_MASS_ANALYZER_TSR = "Term Source REF.4";
    public static final String MS_PROTOCOL_MASS_ANALYZER_TAN = "Term Accession Number.4";
    public static final String MS_PROTOCOL_MS_ASSAY_NAME = "MS Assay Name";
    public static final String MS_PROTOCOL_RAW_SPECTRAL_DATA_FILE = "Raw Spectral Data File";

    /*
     NMR spectroscopy protocol specific for NMR
      */

    public static final String NMR_PROTOCOL_REF = "Protocol REF.2";
    public static final String NMR_PROTOCOL_INSTRUMENT = "Parameter Value[Instrument]";
    public static final String NMR_PROTOCOL_INSTRUMENT_TSR = "Term Source REF.4";
    public static final String NMR_PROTOCOL_INSTRUMENT_TAN = "Term Accession Number.4";
    public static final String NMR_PROTOCOL_NMR_PROBE = "Parameter Value[NMR Probe]";
    public static final String NMR_PROTOCOL_NMR_PROBE_TSR = "Term Source REF.5";
    public static final String NMR_PROTOCOL_NMR_PROBE_TAN = "Term Accession Number.5";
    public static final String NMR_PROTOCOL_NO_OF_TRANSIENTS = "Parameter Value[Number of transients]";
    public static final String NMR_PROTOCOL_PULSE_SEQ_NAME = "Parameter Value[Pulse sequence name]";
    public static final String NMR_PROTOCOL_MAGNETIC_FIELD_STRENGTH = "Parameter Value[Magnetic field strength]";
    public static final String NMR_PROTOCOL_UNIT = "Unit.1";
    public static final String NMR_PROTOCOL_UNIT_TSR = "Term Source REF.6";
    public static final String NMR_PROTOCOL_UNIT_TAN = "Term Accession Number.6";
    public static final String NMR_PROTOCOL_ACQUISITION_PM_DATA_FILE = "Acquisition Parameter Data File";

     /*
     Data transformation protocol common for NMR and MS
      */

    public static final String DATA_TRANSFORMATION_PROTOCOL_REF = "Protocol REF.4";
    public static final String DATA_TRANSFORMATION_PROTOCOL_NORMALIZATION_NAME = "Normalization Name";
    public static final String DATA_TRANSFORMATION_PROTOCOL_DERIVED_SPECTRAL_FILE = "Derived Spectral Data File";

     /*
    NMR assay protocol specific for NMR
      */

    public static final String NMR_ASSAY_PROTOCOL_REF = "Protocol REF.3";
    public static final String NMR_ASSAY_PROTOCOL_NAME = "NMR Assay Name";
    public static final String NMR_ASSAY_FID_FILE = "Free Induction Decay Data File";

      /*
     Metabolite identification  protocol common for NMR and MS
      */

    public static final String METABOLITE_IDENTIFICATION_PROTOCOL_REF = "Protocol REF.5";
    public static final String METABOLITE_IDENTIFICATION_PROTOCOL_TRANSFORMATION_NAME = "Data Transformation Name";
    public static final String METABOLITE_IDENTIFICATION_PROTOCOL_METABOLITE_ASSIGNMENT_FILE = "Metabolite Assignment File";


}
