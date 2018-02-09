package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.metabolights.model.CharacteristicType;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

/**
 * Created by kalai on 13/12/2017.
 */
public class USIPublicationToMLPublication implements Converter<Publication, uk.ac.ebi.subs.metabolights.model.StudyPublication> {
    @Override
    public uk.ac.ebi.subs.metabolights.model.StudyPublication convert(Publication source) {
        if(source == null)return null;
        uk.ac.ebi.subs.metabolights.model.StudyPublication publication = new uk.ac.ebi.subs.metabolights.model.StudyPublication();
        publication.setDoi(source.getDoi());
        publication.setAuthorList(source.getAuthors());
        publication.setTitle(source.getArticleTitle());
        publication.setPubMedID(source.getPubmedId());
        if(source.getStatus()!=null){
            CharacteristicType status = new CharacteristicType();
            status.setAnnotationValue(source.getStatus().name());
            publication.setStatus(status);
        }
        return publication;
    }
}
