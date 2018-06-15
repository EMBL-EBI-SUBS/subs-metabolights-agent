package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.metabolights.model.Project;
import uk.ac.ebi.subs.metabolights.model.Study;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalai on 15/06/2018.
 */
public class MLProjectToUSIProject implements Converter<Project, uk.ac.ebi.subs.data.submittable.Project> {

    MLContactsToUSIContacts toUsiContacts = new MLContactsToUSIContacts();
    MLPublicationToUSIPublication toUSIPublication = new MLPublicationToUSIPublication();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public uk.ac.ebi.subs.data.submittable.Project convert(Project source) {

        uk.ac.ebi.subs.data.submittable.Project project  = new uk.ac.ebi.subs.data.submittable.Project();

        //todo refactor project alias and accession
        //todo currently  ML specific OntologySourceReferences are currently excluded from mapping
        //project.setAlias(source.getIdentifier());
        project.setAccession(source.getIdentifier());
        project.setTitle(source.getTitle());
        project.setDescription(source.getDescription());
        if(source.getPublicReleaseDate()!=null && !source.getPublicReleaseDate().isEmpty()){
            project.setReleaseDate(LocalDate.parse(source.getPublicReleaseDate(), formatter));
        }

        project.setContacts(convertProjectAndStudyContacts(source));
        project.setPublications(convertProjectAndStudyPublications(source));

        return project;
    }

    private List<Contact> convertProjectAndStudyContacts(Project project) {
        List<Contact> usiContacts = new ArrayList<>();
        if(project.getPeople().size() > 0){
            usiContacts.addAll(convertContacts(project.getPeople()));
        }
        for (Study study : project.getStudies()) {
            usiContacts.addAll(convertContacts(study.getPeople()));
        }
        return usiContacts;
    }

    private List<Contact> convertContacts(List<uk.ac.ebi.subs.metabolights.model.Contact> studyContacts) {
        List<Contact> usiContacts = new ArrayList<>();
        for (uk.ac.ebi.subs.metabolights.model.Contact studyContact : studyContacts) {
            uk.ac.ebi.subs.data.component.Contact usiContact = toUsiContacts.convert(studyContact);
            usiContacts.add(usiContact);
        }
        return usiContacts;
    }

    private List<uk.ac.ebi.subs.data.component.Publication> convertProjectAndStudyPublications(Project project) {
        List<uk.ac.ebi.subs.data.component.Publication> usiPublications = new ArrayList<>();
        if(project.getPublications().size() > 0){
            usiPublications.addAll(convertPublications(project.getPublications()));
        }
        for (Study study : project.getStudies()) {
           usiPublications.addAll(convertPublications(study.getPublications()));
        }
        return usiPublications;
    }


    private List<uk.ac.ebi.subs.data.component.Publication> convertPublications(List<uk.ac.ebi.subs.metabolights.model.Publication> publications) {
        List<uk.ac.ebi.subs.data.component.Publication> usiPublications = new ArrayList<>();
        for (uk.ac.ebi.subs.metabolights.model.Publication publication : publications) {
            uk.ac.ebi.subs.data.component.Publication usiPublication = toUSIPublication.convert(publication);
            usiPublications.add(usiPublication);
        }
        return usiPublications;
    }

}
