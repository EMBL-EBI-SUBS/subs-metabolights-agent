package uk.ac.ebi.subs.metabolights.agent;

import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.data.submittable.Study;
import uk.ac.ebi.subs.metabolights.model.Factor;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;

import java.util.List;

public class AgentProcessorUtils {

    public static boolean containsValue(List entries) {
        return entries != null && entries.size() > 0;
    }

    public static boolean alreadyPresent(List<Factor> factors, String factorAttributeName) {
        for (Factor factor : factors) {
            if (isValid(factor.getFactorName())) {
                if (factor.getFactorName().equalsIgnoreCase(factorAttributeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<OntologyModel> descriptors, String descriptorAttributeName) {
        for (OntologyModel descriptor : descriptors) {
            if (isValid(descriptor.getAnnotationValue())) {
                if (descriptor.getAnnotationValue().equalsIgnoreCase(descriptorAttributeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<uk.ac.ebi.subs.metabolights.model.Contact> mlContacts, Contact usiContact) {
        for (uk.ac.ebi.subs.metabolights.model.Contact mlContact : mlContacts) {
            if (isValid(mlContact.getEmail())) {
                if (mlContact.getEmail().equalsIgnoreCase(usiContact.getEmail())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<uk.ac.ebi.subs.metabolights.model.Publication> mlPublications, Publication usiPublication) {
        for (uk.ac.ebi.subs.metabolights.model.Publication mlPublication : mlPublications) {
            if (isValid(mlPublication.getTitle())) {
                if (mlPublication.getTitle().equalsIgnoreCase(usiPublication.getArticleTitle())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean alreadyHas(List<uk.ac.ebi.subs.metabolights.model.Protocol> mlProtocols, Protocol usiProtocol) {
        for (uk.ac.ebi.subs.metabolights.model.Protocol mlProtocol : mlProtocols) {
            if (isValid(mlProtocol.getName())) {
                if (mlProtocol.getName().equalsIgnoreCase(usiProtocol.getTitle())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPresent(Study study, String attributeType) {
        return study.getAttributes().get(attributeType) != null && !study.getAttributes().get(attributeType).isEmpty();
    }

    public static boolean isValid(String object) {
        return object != null && !object.isEmpty();
    }

}
