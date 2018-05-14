package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

import java.util.Arrays;

/**
 * Created by kalai on 13/12/2017.
 */
public class USIContactsToMLContacts implements Converter<Contact, uk.ac.ebi.subs.metabolights.model.Contact> {
    @Override
    public uk.ac.ebi.subs.metabolights.model.Contact convert(Contact source) {
        if (source == null) {
            return null;
        }
        uk.ac.ebi.subs.metabolights.model.Contact mlContact = new uk.ac.ebi.subs.metabolights.model.Contact();
        mlContact.setFirstName(source.getFirstName());
        mlContact.setLastName(source.getLastName());
        mlContact.setMidInitials(source.getMiddleInitials());
        mlContact.setAddress(source.getAddress());
        mlContact.setAffiliation(source.getAffiliation());
        mlContact.setEmail(source.getEmail());
        mlContact.setFax(source.getFax());
        mlContact.setOrcid(source.getOrcid());
        //todo Should roled be Ontology term within USI
        if (source.getRoles().size() > 0) {
            OntologyModel roleType = new OntologyModel();
            roleType.setAnnotationValue(source.getRoles().get(0));
            mlContact.setRoles(Arrays.asList(roleType));
        }
        return mlContact;
    }
}
