package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.metabolights.model.Contact;
import uk.ac.ebi.subs.metabolights.model.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalai on 15/06/2018.
 */
public class USIProjectToMLProject implements Converter<uk.ac.ebi.subs.data.submittable.Project, Project> {
    USIContactsToMLContacts usiContactsToMLContacts = new USIContactsToMLContacts();
    USIPublicationToMLPublication usiPublicationToMLPublication = new USIPublicationToMLPublication();

    @Override
    public Project convert(uk.ac.ebi.subs.data.submittable.Project source) {
        Project project = new Project();
        project.setIdentifier(source.getAccession());
        project.setTitle(source.getTitle());
        project.setDescription(source.getDescription());
        if (source.getReleaseDate() != null) {
            project.setPublicReleaseDate(source.getReleaseDate().toString());
        }
        project.setPeople(convertContacts(source.getContacts()));
        project.setPublications(convertPublications(source.getPublications()));
        return project;
    }

    private List<Contact> convertContacts(List<uk.ac.ebi.subs.data.component.Contact> contacts) {
        List<uk.ac.ebi.subs.metabolights.model.Contact> people = new ArrayList<>();
        for (uk.ac.ebi.subs.data.component.Contact contact : contacts) {
            people.add(usiContactsToMLContacts.convert(contact));
        }
        return people;
    }

    private List<uk.ac.ebi.subs.metabolights.model.Publication> convertPublications(List<Publication> publications) {
        List<uk.ac.ebi.subs.metabolights.model.Publication> mlPublications = new ArrayList<>();
        for (Publication publication : publications) {
            mlPublications.add(usiPublicationToMLPublication.convert(publication));
        }
        return mlPublications;
    }
}
