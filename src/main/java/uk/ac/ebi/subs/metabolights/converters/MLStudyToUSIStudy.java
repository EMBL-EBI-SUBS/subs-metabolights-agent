package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.metabolights.model.Study;

/**
 * Created by kalai on 18/12/2017.
 */
public class MLStudyToUSIStudy  implements Converter<Study, uk.ac.ebi.subs.data.submittable.Study> {
    @Override
    public uk.ac.ebi.subs.data.submittable.Study convert(Study source) {


        return null;
    }
}
