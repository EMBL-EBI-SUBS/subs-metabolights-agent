package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Publication;

/**
 * Created by kalai on 13/12/2017.
 */
public class USIPublicationToMLPublication implements Converter<Publication, uk.ac.ebi.subs.metabolights.model.Publication> {
    @Override
    public uk.ac.ebi.subs.metabolights.model.Publication convert(Publication source) {
        if(source == null)return null;
        uk.ac.ebi.subs.metabolights.model.Publication publication = new uk.ac.ebi.subs.metabolights.model.Publication();
        publication.setDoi(source.getDoi());
        publication.setAuthorList(source.getAuthors());
        publication.setTitle(source.getArticleTitle());
        publication.setPubMedID(source.getPubmedId());
        return publication;
    }
}
