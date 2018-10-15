package uk.ac.ebi.subs.metabolights.agent;

import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

import java.util.List;

public class AgentProcessorUtils {

    public static boolean containsValue(List entries) {
        return entries != null && entries.size() > 0;
    }

    public static boolean alreadyPresent(List<Factor> factors, String factorAttributeName) {
        for (Factor factor : factors) {
            if (factor.getFactorName().equalsIgnoreCase(factorAttributeName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<OntologyModel> descriptors, String descriptorAttributeName) {
        for (OntologyModel descriptor : descriptors) {
            if (descriptor.getAnnotationValue().equalsIgnoreCase(descriptorAttributeName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<uk.ac.ebi.subs.metabolights.model.Contact> mlContacts, Contact usiContact) {
        for (uk.ac.ebi.subs.metabolights.model.Contact mlContact : mlContacts) {
            if (mlContact.getEmail().equalsIgnoreCase(usiContact.getEmail())) {
                return true;
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<uk.ac.ebi.subs.metabolights.model.Publication> mlPublications, Publication usiPublication) {
        for (uk.ac.ebi.subs.metabolights.model.Publication mlPublication : mlPublications) {
            if (mlPublication.getTitle().equalsIgnoreCase(usiPublication.getArticleTitle())) {
                return true;
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<uk.ac.ebi.subs.metabolights.model.Protocol> mlProtocols, Protocol usiProtocol) {
        for (uk.ac.ebi.subs.metabolights.model.Protocol mlProtocol : mlProtocols) {
            if (mlProtocol.getName().equalsIgnoreCase(usiProtocol.getTitle())) {
                return true;
            }
        }
        return false;
    }

}
