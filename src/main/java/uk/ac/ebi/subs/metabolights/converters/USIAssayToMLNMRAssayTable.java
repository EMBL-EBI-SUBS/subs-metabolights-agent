package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.ProtocolUse;
import uk.ac.ebi.subs.data.component.SampleUse;
import uk.ac.ebi.subs.metabolights.model.Assay;
import uk.ac.ebi.subs.metabolights.model.NMRAssayMap;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class USIAssayToMLNMRAssayTable implements Converter<uk.ac.ebi.subs.data.submittable.Assay, NMRAssayMap> {
    @Override
    public NMRAssayMap convert(uk.ac.ebi.subs.data.submittable.Assay source) {
        Assay assay = new Assay();
        assay.setFilename(source.getAlias());

        Map<String, Collection<Attribute>> usiAssayAttributes = source.getAttributes();
        List<ProtocolUse> protocolUses = source.getProtocolUses();
        List<SampleUse> sampleUses =
                source.getSampleUses();

        //todo parse sample, protocol and attribute values to contruct a single row in NMR assay Table

        return null;
    }

    private void parseSample(List<SampleUse> sampleUses) {
        if (sampleUses.isEmpty() && sampleUses.size() == 1) {
           //todo extract sample name
        }
    }

    private void parse(List<ProtocolUse> protocolUses) {
        if (!protocolUses.isEmpty()) {
            for (ProtocolUse protocolUse : protocolUses) {
                if (protocolUse.getProtocolRef().getAlias().equals("Extraction")) {
                    //todo set extraction values
                }
                if (protocolUse.getProtocolRef().getAlias().equals("NMR sample")) {
                    //todo set extraction values
                }
                if (protocolUse.getProtocolRef().getAlias().equals("NMR spectroscopy")) {
                    //todo set extraction values
                }
                if (protocolUse.getProtocolRef().getAlias().equals("NMR assay")) {
                    //todo set extraction values
                }
                if (protocolUse.getProtocolRef().getAlias().equals("Data transformation")) {
                    //todo set extraction values
                }
                if (protocolUse.getProtocolRef().getAlias().equals("Metabolite identification")) {
                    //todo set extraction values
                }
            }
        }
    }
}
