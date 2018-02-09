package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.metabolights.model.Study;

/**
 * Created by kalai on 18/12/2017.
 */
public class USIStudyToMLStudy implements Converter<uk.ac.ebi.subs.data.submittable.Study, Study> {
    @Override
    public Study convert(uk.ac.ebi.subs.data.submittable.Study source) {
        //todo convert project/source into ML project.
        return null;
    }
}
