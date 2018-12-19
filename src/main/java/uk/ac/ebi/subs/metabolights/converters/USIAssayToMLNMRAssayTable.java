package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.metabolights.model.NMRAssayMap;

import java.util.Collection;
import java.util.Map;

public class USIAssayToMLNMRAssayTable implements Converter<uk.ac.ebi.subs.data.submittable.Assay, NMRAssayMap> {
    @Override
    public NMRAssayMap convert(uk.ac.ebi.subs.data.submittable.Assay source) {

        NMRAssayMap nmrAssayMap = new NMRAssayMap(source);

        Map<String, Collection<Attribute>> usiAssayAttributes = source.getAttributes();
        //todo decide what to capture using attributes and how to use it
        return nmrAssayMap;
    }
}
