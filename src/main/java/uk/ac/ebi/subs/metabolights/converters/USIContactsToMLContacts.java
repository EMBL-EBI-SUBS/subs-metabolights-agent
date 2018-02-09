package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Contacts;
import uk.ac.ebi.subs.metabolights.model.CharacteristicType;
import uk.ac.ebi.subs.metabolights.model.StudyContact;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kalai on 13/12/2017.
 */
public class USIContactsToMLContacts implements Converter<Contact, StudyContact> {
    @Override
    public StudyContact convert(Contact source) {
        if (source == null) {
            return null;
        }
        StudyContact mlContact = new StudyContact();
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
            CharacteristicType roleType = new CharacteristicType();
            roleType.setAnnotationValue(source.getRoles().get(0));
            mlContact.setRoles(Arrays.asList(roleType));
        }
        return mlContact;
    }
}
