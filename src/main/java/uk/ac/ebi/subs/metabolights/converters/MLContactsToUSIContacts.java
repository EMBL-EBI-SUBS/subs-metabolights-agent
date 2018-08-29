package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Contact;

import java.util.Arrays;

/**
 * Created by kalai on 13/12/2017.
 */
public class MLContactsToUSIContacts implements Converter<uk.ac.ebi.subs.metabolights.model.Contact, Contact> {
    @Override
    public Contact convert(uk.ac.ebi.subs.metabolights.model.Contact source) {
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
        usiContact.setPhone(source.getPhone());
        usiContact.setOrcid(source.getOrcid());
        //todo conversion of roles - USI role to be ontology model?
        if(source.getRoles() != null && source.getRoles().size()>0){
            usiContact.setRoles(Arrays.asList(source.getRoles().get(0).getAnnotationValue()));
        }
        return usiContact;
    }
}
