package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.ProtocolUse;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.metabolights.model.Assay;
import uk.ac.ebi.subs.metabolights.model.AssaySpreadSheetConstants;
import uk.ac.ebi.subs.metabolights.model.NMRAssayMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class USIAssayToMLNMRAssayTable implements Converter<uk.ac.ebi.subs.data.submittable.Assay, NMRAssayMap> {
    @Override
    public NMRAssayMap convert(uk.ac.ebi.subs.data.submittable.Assay source) {
        Assay assay = new Assay();
        assay.setFilename(source.getAlias());

        NMRAssayMap nmrAssayMap = new NMRAssayMap();

        Map<String, Collection<Attribute>> usiAssayAttributes = source.getAttributes();
        //todo decide what to capture using attributes and how to use it

        List<ProtocolUse> protocolUses = source.getProtocolUses();
        List<SampleUse> sampleUses =
                source.getSampleUses();
        parseSample(sampleUses, nmrAssayMap);
        parseProtocol(protocolUses, nmrAssayMap);

        return nmrAssayMap;
    }

    private void parseSample(List<SampleUse> sampleUses, NMRAssayMap nmrAssayMap) {
        if (!sampleUses.isEmpty() && sampleUses.size() == 1) {
            nmrAssayMap.put(AssaySpreadSheetConstants.SAMPLE_NAME, sampleUses.get(0).getSampleRef().getAlias());
        }
    }

    private void parseProtocol(List<ProtocolUse> protocolUses, NMRAssayMap nmrAssayMap) {
        if (!protocolUses.isEmpty()) {
            for (ProtocolUse protocolUse : protocolUses) {
                if (protocolUse.getProtocolRef().getAlias().equals("Extraction")) {
                    parseExtraction(protocolUse, nmrAssayMap);
                }
                if (protocolUse.getProtocolRef().getAlias().equals("NMR sample")) {
                    parseNMRSample(protocolUse, nmrAssayMap);
                }
                if (protocolUse.getProtocolRef().getAlias().equals("NMR spectroscopy")) {
                    parseNMRSpectroscopy(protocolUse, nmrAssayMap);
                }
                if (protocolUse.getProtocolRef().getAlias().equals("NMR assay")) {
                    parseNMRAssay(protocolUse, nmrAssayMap);
                }
                if (protocolUse.getProtocolRef().getAlias().equals("Data transformation")) {
                    parseDataTransformation(protocolUse, nmrAssayMap);
                }
                if (protocolUse.getProtocolRef().getAlias().equals("Metabolite identification")) {
                    parseMetaboliteIdentification(protocolUse, nmrAssayMap);
                }
            }
        }
    }

    private void parseExtraction(ProtocolUse extraction, NMRAssayMap nmrAssayMap) {
        nmrAssayMap.put(AssaySpreadSheetConstants.EXTRACTION_PROTOCOL_REF, "Extraction");

        if (extraction.getAttributes().size() > 0) {
            if (extraction.getAttributes().containsKey("Extraction Method")) {
                Attribute extraction_method = extraction.getAttributes().get("Extraction Method").iterator().next();
                if (extraction_method != null && extraction_method.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.EXTRACTION_PROTOCOL_EXTRACTION_METHOD, extraction_method.getValue());
                }
            }
            if (extraction.getAttributes().containsKey("Extract Name")) {
                Attribute extract_name = extraction.getAttributes().get("Extract Name").iterator().next();
                if (extract_name != null && extract_name.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.EXTRACTION_PROTOCOL_EXTRACT_NAME, extract_name.getValue());
                }
            }
        }
    }

    private void parseNMRSample(ProtocolUse nmrSample, NMRAssayMap nmrAssayMap) {
        nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_REF, "NMR sample");
        if (nmrSample.getAttributes().size() > 0) {
            if (nmrSample.getAttributes().containsKey("NMR tube type")) {
                Attribute nmr_tube_type = nmrSample.getAttributes().get("NMR tube type").iterator().next();
                if (nmr_tube_type != null && nmr_tube_type.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TUBE_TYPE, nmr_tube_type.getValue());
                    if (nmr_tube_type.getTerms() != null && nmr_tube_type.getTerms().size() == 1) {
                        if (nmr_tube_type.getTerms().get(0).getUrl() != null || !nmr_tube_type.getTerms().get(0).getUrl().isEmpty()) {
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TUBE_TYPE_TSR, nmr_tube_type.getTerms().get(0).getUrl());
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TUBE_TYPE_TAN, "");
                            //todo term accession number not set
                        }
                    }
                }
            }
            if (nmrSample.getAttributes().containsKey("Solvent")) {
                Attribute solvent = nmrSample.getAttributes().get("Solvent").iterator().next();
                if (solvent != null && solvent.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_SOLVENT, solvent.getValue());
                    if (solvent.getTerms() != null && solvent.getTerms().size() == 1) {
                        if (solvent.getTerms().get(0).getUrl() != null || !solvent.getTerms().get(0).getUrl().isEmpty()) {
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_SOLVENT_TSR, solvent.getTerms().get(0).getUrl());
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_SOLVENT_TAN, "");
                            //todo term accession number not set
                        }
                    }
                }
            }
            if (nmrSample.getAttributes().containsKey("Sample pH")) {
                Attribute samplePH = nmrSample.getAttributes().get("Sample pH").iterator().next();
                if (samplePH != null && samplePH.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_SAMPLE_PH, samplePH.getValue());
                }
            }
            if (nmrSample.getAttributes().containsKey("Temperature")) {
                Attribute temperature = nmrSample.getAttributes().get("Temperature").iterator().next();
                if (temperature != null && temperature.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TEMPERATURE, temperature.getValue());
                    if (temperature.getTerms() != null && temperature.getTerms().size() == 1) {
                        if (temperature.getTerms().get(0).getUrl() != null || !temperature.getTerms().get(0).getUrl().isEmpty()) {
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TEMPERATURE_UNIT_TSR, temperature.getTerms().get(0).getUrl());
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_TEMPERATURE_UNIT_TAN, "");
                            //todo term accession number not set
                        }
                    }

                    if (temperature.getUnits() != null && !temperature.getUnits().isEmpty()) {
                        nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_UNIT, temperature.getUnits());
                    }
                }
            }
            if (nmrSample.getAttributes().containsKey("Labeled Extract Name")) {
                Attribute labeled_extract_name = nmrSample.getAttributes().get("Labeled Extract Name").iterator().next();
                if (labeled_extract_name != null && labeled_extract_name.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_EXTRACT_NAME, labeled_extract_name.getValue());
                }
            }
            if (nmrSample.getAttributes().containsKey("Label")) {
                Attribute label = nmrSample.getAttributes().get("Label").iterator().next();
                if (label != null && label.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_LABEL, label.getValue());
                    if (label.getTerms() != null && label.getTerms().size() == 1) {
                        if (label.getTerms().get(0).getUrl() != null || !label.getTerms().get(0).getUrl().isEmpty()) {
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_EXTRACT_NAME_TSR, label.getTerms().get(0).getUrl());
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_SAMPLE_PROTOCOL_EXTRACT_NAME_TAN, "");
                            //todo term accession number not set
                        }
                    }
                }
            }
        }

    }

    private void parseNMRAssay(ProtocolUse nmrAssay, NMRAssayMap nmrAssayMap) {
        nmrAssayMap.put(AssaySpreadSheetConstants.NMR_ASSAY_PROTOCOL_REF, "NMR assay");
        if (nmrAssay.getAttributes().size() > 0) {
            if (nmrAssay.getAttributes().containsKey("NMR Assay Name")) {
                Attribute nmr_assay_name = nmrAssay.getAttributes().get("NMR Assay Name").iterator().next();
                if (nmr_assay_name != null && nmr_assay_name.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_ASSAY_PROTOCOL_NAME, nmr_assay_name.getValue());
                }
            }
            if (nmrAssay.getAttributes().containsKey("Free Induction Decay Data File")) {
                Attribute free_induction_decay_data_file = nmrAssay.getAttributes().get("Free Induction Decay Data File").iterator().next();
                if (free_induction_decay_data_file != null && free_induction_decay_data_file.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_ASSAY_FID_FILE, free_induction_decay_data_file.getValue());
                }
            }
        }
    }

    private void parseNMRSpectroscopy(ProtocolUse nmrSpectroscopy, NMRAssayMap nmrAssayMap) {

        nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_REF, "NMR spectroscopy");
        if (nmrSpectroscopy.getAttributes().size() > 0) {
            if (nmrSpectroscopy.getAttributes().containsKey("Instrument")) {
                Attribute instrument = nmrSpectroscopy.getAttributes().get("Instrument").iterator().next();
                if (instrument != null && instrument.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_INSTRUMENT, instrument.getValue());
                    if (instrument.getTerms() != null && instrument.getTerms().size() == 1) {
                        if (instrument.getTerms().get(0).getUrl() != null || !instrument.getTerms().get(0).getUrl().isEmpty()) {
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_INSTRUMENT_TSR, instrument.getTerms().get(0).getUrl());
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_INSTRUMENT_TAN, "");
                            //todo term accession number not set
                        }
                    }
                }
            }
            if (nmrSpectroscopy.getAttributes().containsKey("NMR Probe")) {
                Attribute nmr_Probe = nmrSpectroscopy.getAttributes().get("NMR Probe").iterator().next();
                if (nmr_Probe != null && nmr_Probe.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_NMR_PROBE, nmr_Probe.getValue());
                    if (nmr_Probe.getTerms() != null && nmr_Probe.getTerms().size() == 1) {
                        if (nmr_Probe.getTerms().get(0).getUrl() != null || !nmr_Probe.getTerms().get(0).getUrl().isEmpty()) {
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_NMR_PROBE_TSR, nmr_Probe.getTerms().get(0).getUrl());
                            nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_NMR_PROBE_TAN, "");
                            //todo term accession number not set
                        }
                    }
                }
            }

            if (nmrSpectroscopy.getAttributes().containsKey("Number of transients")) {
                Attribute transients = nmrSpectroscopy.getAttributes().get("Number of transients").iterator().next();
                if (transients != null && transients.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_NO_OF_TRANSIENTS, transients.getValue());
                }
            }

            if (nmrSpectroscopy.getAttributes().containsKey("Pulse sequence name")) {
                Attribute pulse_sequence_name = nmrSpectroscopy.getAttributes().get("Pulse sequence name").iterator().next();
                if (pulse_sequence_name != null && pulse_sequence_name.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_PULSE_SEQ_NAME, pulse_sequence_name.getValue());
                }
            }

            if (nmrSpectroscopy.getAttributes().containsKey("Magnetic field strength")) {
                Attribute magnetic_field_strength = nmrSpectroscopy.getAttributes().get("Magnetic field strength").iterator().next();
                if (magnetic_field_strength != null && magnetic_field_strength.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_MAGNETIC_FIELD_STRENGTH, magnetic_field_strength.getValue());

                    if (magnetic_field_strength.getUnits() != null && !magnetic_field_strength.getUnits().isEmpty()) {
                        nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_UNIT, magnetic_field_strength.getUnits());
                        nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_UNIT_TSR, "");
                        nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_UNIT_TAN, "");
                        //todo term accession number and term source not set for unit
                    }
                }
            }

            if (nmrSpectroscopy.getAttributes().containsKey("Acquisition Parameter Data File")) {
                Attribute acquisition_parameter_data_file = nmrSpectroscopy.getAttributes().get("Acquisition Parameter Data File").iterator().next();
                if (acquisition_parameter_data_file != null && acquisition_parameter_data_file.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.NMR_PROTOCOL_ACQUISITION_PM_DATA_FILE, acquisition_parameter_data_file.getValue());
                }
            }
        }

    }

    private void parseDataTransformation(ProtocolUse dataTransformation, NMRAssayMap nmrAssayMap) {
        nmrAssayMap.put(AssaySpreadSheetConstants.DATA_TRANSFORMATION_PROTOCOL_REF, "Data transformation");

        if (dataTransformation.getAttributes().size() > 0) {
            if (dataTransformation.getAttributes().containsKey("Normalization Name")) {
                Attribute normalization_name = dataTransformation.getAttributes().get("Normalization Name").iterator().next();
                if (normalization_name != null && normalization_name.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.DATA_TRANSFORMATION_PROTOCOL_NORMALIZATION_NAME, normalization_name.getValue());
                }
            }
            if (dataTransformation.getAttributes().containsKey("Derived Spectral Data File")) {
                Attribute derived_spectral_data_file = dataTransformation.getAttributes().get("Derived Spectral Data File").iterator().next();
                if (derived_spectral_data_file != null && derived_spectral_data_file.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.DATA_TRANSFORMATION_PROTOCOL_DERIVED_SPECTRAL_FILE, derived_spectral_data_file.getValue());
                }
            }
        }
    }

    private void parseMetaboliteIdentification(ProtocolUse metaboliteIdentification, NMRAssayMap nmrAssayMap) {
        nmrAssayMap.put(AssaySpreadSheetConstants.METABOLITE_IDENTIFICATION_PROTOCOL_REF, "Metabolite identification");
        if (metaboliteIdentification.getAttributes().size() > 0) {
            if (metaboliteIdentification.getAttributes().containsKey("Data Transformation Name")) {
                Attribute data_transformation_name = metaboliteIdentification.getAttributes().get("Data Transformation Name").iterator().next();
                if (data_transformation_name != null && data_transformation_name.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.METABOLITE_IDENTIFICATION_PROTOCOL_TRANSFORMATION_NAME, data_transformation_name.getValue());
                }
            }
            if (metaboliteIdentification.getAttributes().containsKey("Metabolite Assignment File")) {
                Attribute metabolite_assignment_file = metaboliteIdentification.getAttributes().get("Metabolite Assignment File").iterator().next();
                if (metabolite_assignment_file != null && metabolite_assignment_file.getValue() != null) {
                    nmrAssayMap.put(AssaySpreadSheetConstants.METABOLITE_IDENTIFICATION_PROTOCOL_METABOLITE_ASSIGNMENT_FILE, metabolite_assignment_file.getValue());
                }
            }
        }
    }
}
