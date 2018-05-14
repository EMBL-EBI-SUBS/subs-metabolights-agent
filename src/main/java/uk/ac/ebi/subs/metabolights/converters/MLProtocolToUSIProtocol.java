package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;
import uk.ac.ebi.subs.metabolights.model.ProtocolParameter;

import java.util.*;

/**
 * Created by kalai on 01/02/2018.
 */
public class MLProtocolToUSIProtocol implements Converter<uk.ac.ebi.subs.metabolights.model.Protocol, Protocol> {
    @Override
    public Protocol convert(uk.ac.ebi.subs.metabolights.model.Protocol source) {
        Protocol protocol = new Protocol();
        Map<String, Collection<Attribute>> attributes = new HashMap<>();

        protocol.setAlias(source.getName());
        protocol.setDescription(source.getDescription());
        protocol.setId(source.getUri());
        //todo URI is given for id
        // convert protocol type
        OntologyModel protocolType = source.getProtocolType();
        if (protocolType != null) {
            if (protocolType.getAnnotationValue() != null && !protocolType.getAnnotationValue().isEmpty()) {
                Attribute attribute = new Attribute();
                attribute.setValue(protocolType.getAnnotationValue());
                attributes.put("protocolType", Arrays.asList(attribute));
            }
        }
        // convert StudyProtocolParameters
        if (source.getParameters() != null && source.getParameters().size() > 0) {
            List<Attribute> protocolParameterAttributes = new ArrayList<>();
            for (ProtocolParameter protocolParameter : source.getParameters()) {
                Attribute attribute = new Attribute();
                attribute.setValue(protocolParameter.getParameterName().getAnnotationValue());
                protocolParameterAttributes.add(attribute);
            }
            attributes.put("parameters", protocolParameterAttributes);
        }
        protocol.setAttributes(attributes);
        return protocol;
    }
}
