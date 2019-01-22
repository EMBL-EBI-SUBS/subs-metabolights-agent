package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kalai on 13/12/2017.
 */
@Service
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
       // mlContact.setOrcid(source.getOrcid());
        mlContact.setPhone(source.getPhone());
        //todo Should roled be Ontology term within USI
        if (source.getRoles()!=null && source.getRoles().size() > 0) {
            OntologyModel roleType = new OntologyModel();
            roleType.setAnnotationValue(source.getRoles().get(0));
            mlContact.setRoles(Arrays.asList(roleType));
        } else{
            OntologyModel roleType = new OntologyModel();
            mlContact.setRoles(Arrays.asList(roleType));
        }
        mlContact.setComments(new ArrayList<>());
        return mlContact;
    }

    public List<uk.ac.ebi.subs.metabolights.model.Contact> convert(List<Contact> contacts){
        List<uk.ac.ebi.subs.metabolights.model.Contact> mlContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            mlContacts.add(convert(contact));
        }
        return mlContacts;
    }
}
