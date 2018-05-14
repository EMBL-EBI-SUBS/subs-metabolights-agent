package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.AssayRef;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.StudyRef;
import uk.ac.ebi.subs.data.component.Term;
import uk.ac.ebi.subs.data.submittable.AssayData;
import uk.ac.ebi.subs.metabolights.model.Assay;
import uk.ac.ebi.subs.metabolights.model.MLFile;

import java.util.*;

/**
 * Created by kalai on 29/01/2018.
 */
public class MLAssayToUSIAssay implements Converter<Assay, uk.ac.ebi.subs.data.submittable.Assay> {
    @Override
    public uk.ac.ebi.subs.data.submittable.Assay convert(Assay source) {
        uk.ac.ebi.subs.data.submittable.Assay assay = new uk.ac.ebi.subs.data.submittable.Assay();
        assay.setAlias(source.getFilename());

        Map<String, Collection<Attribute>> assayAttributes = assay.getAttributes();
        if (source.getTechnologyType() != null) {
            Attribute technologyType = new Attribute();
            technologyType.setValue(source.getTechnologyType().getAnnotationValue());
            Term term = new Term();
            term.setUrl(source.getTechnologyType().getTermAccession());
            technologyType.setTerms(Arrays.asList(term));
            assayAttributes.put("technologyType", Arrays.asList(technologyType));
        }

        if (source.getMeasurementType() != null) {
            Attribute measurementType = new Attribute();
            measurementType.setValue(source.getMeasurementType().getAnnotationValue());
            Term term = new Term();
            term.setUrl(source.getMeasurementType().getTermAccession());
            measurementType.setTerms(Arrays.asList(term));
            assayAttributes.put("measurementType", Arrays.asList(measurementType));
        }

        Attribute technologyPlatformAttribute = new Attribute();
        technologyPlatformAttribute.setValue(source.getTechnologyPlatform());
        assayAttributes.put("technologyPlatform", Arrays.asList(technologyPlatformAttribute));

        assay.setAttributes(assayAttributes);

        return assay;
    }
}

//MLFileToUSIFile toUSIFileConverter = new MLFileToUSIFile();

//        List<AssayData> assayDataList = new ArrayList<>();
//        List<AssayRef> assayRefs = Arrays.asList((AssayRef) assay.asRef());
//
//        if (source.getDataFiles() != null && source.getDataFiles().size() > 0) {
//            for (MLFile mlfile : source.getDataFiles()) {
//                AssayData assayData = new AssayData();
//               // assayData.setTeam(team); todo fix this crosslinking
//                assayData.setAssayRefs(assayRefs);
//                if (mlfile != null) {
//                    assayData.setFiles(Arrays.asList(toUSIFileConverter.convert(mlfile)));
//                    assayDataList.add(assayData);
//                }
//            }
//        }


