package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

import java.util.ArrayList;

/**
 * Created by kalai on 13/12/2017.
 */
@Service
public class USIPublicationToMLPublication implements Converter<Publication, uk.ac.ebi.subs.metabolights.model.Publication> {
    @Override
    public uk.ac.ebi.subs.metabolights.model.Publication convert(Publication source) {
        if(source == null)return null;
        uk.ac.ebi.subs.metabolights.model.Publication publication = new uk.ac.ebi.subs.metabolights.model.Publication();
        publication.setDoi(source.getDoi());
        publication.setAuthorList(source.getAuthors());
        publication.setTitle(source.getArticleTitle());
        publication.setPubMedID(source.getPubmedId());
        OntologyModel status = new OntologyModel();
        status.setComments(new ArrayList<>());
        status.setTermAccession("");
        //todo termAccession is ignored to set

        if(source.getStatus()!=null){
            status.setAnnotationValue(source.getStatus().name());
            publication.setStatus(status);
        } else{
            status.setAnnotationValue("");
            publication.setStatus(status);
        }
        publication.setComments(new ArrayList<>());
        return publication;
    }
}
