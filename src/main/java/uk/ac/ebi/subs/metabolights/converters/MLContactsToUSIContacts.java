package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Contacts;
import uk.ac.ebi.subs.metabolights.model.StudyContact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalai on 13/12/2017.
 */
public class MLContactsToUSIContacts implements Converter<StudyContact, Contact> {
    @Override
    public Contact convert(StudyContact source) {
        if (source == null) {
            return null;
        }
        Contact usiContact = new Contact();
        usiContact.setFirstName(source.getFirstName());
        usiContact.setLastName(source.getLastName());
        usiContact.setMiddleInitials(source.getMidInitials());
        usiContact.setAddress(source.getAddress());
        usiContact.setAffiliation(source.getAffiliation());
        usiContact.setEmail(source.getEmail());
        usiContact.setFax(source.getFax());
        usiContact.setOrcid(source.getOrcid());
        //todo conversion of roles
        return usiContact;
    }
}
