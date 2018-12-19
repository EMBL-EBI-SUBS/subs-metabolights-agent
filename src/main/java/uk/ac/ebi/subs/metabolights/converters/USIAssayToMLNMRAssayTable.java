package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.ProtocolUse;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.metabolights.model.Assay;
import uk.ac.ebi.subs.metabolights.model.AssayMap;
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

        NMRAssayMap nmrAssayMap = new NMRAssayMap(source);

        AssayMap nmrAssayMap1 =  new NMRAssayMap(source);

        if(nmrAssayMap1 instanceof NMRAssayMap){

        }

        Map<String, Collection<Attribute>> usiAssayAttributes = source.getAttributes();
        //todo decide what to capture using attributes and how to use it

        //todo migrate methods

        return nmrAssayMap;
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
