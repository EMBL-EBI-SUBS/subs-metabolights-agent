package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.PublicationStatus;
import uk.ac.ebi.subs.metabolights.model.Publication;

/**
 * Created by kalai on 13/12/2017.
 */
public class MLPublicationToUSIPublication implements Converter<Publication, uk.ac.ebi.subs.data.component.Publication> {
    @Override
    public uk.ac.ebi.subs.data.component.Publication convert(Publication source) {
        if (source == null) {
            return null;
        }
        uk.ac.ebi.subs.data.component.Publication publication = new uk.ac.ebi.subs.data.component.Publication();
        publication.setArticleTitle(source.getTitle());
        publication.setAuthors(source.getAuthorList());
        publication.setDoi(source.getDoi());
        publication.setPubmedId(source.getPubMedID());
        if(source.getStatus()!= null && source.getStatus().getAnnotationValue()!=null){
            if(source.getStatus().getAnnotationValue().equalsIgnoreCase("Published")){
                publication.setStatus(PublicationStatus.Published);
            }
            else if(source.getStatus().getAnnotationValue().equalsIgnoreCase("Submitted")){
                publication.setStatus(PublicationStatus.Submitted);
            }
            //todo very Inprepation
            else if(source.getStatus().getAnnotationValue().equalsIgnoreCase("Inprepation")){
                publication.setStatus(PublicationStatus.InPreparation);
            }
            else{
                publication.setStatus(PublicationStatus.Unknown);
            }

        }
        // todo issue, year include in ML?
        return publication;
    }
}
