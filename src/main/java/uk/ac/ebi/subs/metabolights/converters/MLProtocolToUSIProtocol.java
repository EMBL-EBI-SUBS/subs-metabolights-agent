package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.component.Term;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.model.CharacteristicType;
import uk.ac.ebi.subs.metabolights.model.StudyProtocol;
import uk.ac.ebi.subs.metabolights.model.StudyProtocolParameter;

import java.util.*;

/**
 * Created by kalai on 01/02/2018.
 */
public class MLProtocolToUSIProtocol implements Converter<StudyProtocol, Protocol> {
    @Override
    public Protocol convert(StudyProtocol source) {
        Protocol protocol = new Protocol();
        Map<String, Collection<Attribute>> attributes = new HashMap<>();

        protocol.setAlias(source.getName());
        protocol.setDescription(source.getDescription());
        protocol.setId(source.getId());
        // convert protocol type
        CharacteristicType protocolType = source.getProtocolType();
        if (protocolType != null) {
            if (protocolType.getAnnotationValue() != null && !protocolType.getAnnotationValue().isEmpty()) {
                Attribute attribute = new Attribute();
                attribute.setValue(protocolType.getAnnotationValue());
                attributes.put("protocolType", Arrays.asList(attribute));
                //todo fix cross-referencing ids conversion
            }
        }
        // convert StudyProtocolParameters
        if (source.getParameters() != null && source.getParameters().size() > 0) {
            List<Attribute> protocolParameterAttributes = new ArrayList<>();
            for (StudyProtocolParameter protocolParameter : source.getParameters()) {
                Attribute attribute = new Attribute();
                attribute.setValue(protocolParameter.getParameterName().getAnnotationValue());
                protocolParameterAttributes.add(attribute);
                //todo fix cross-referencing ids conversion
            }
            attributes.put("parameters", protocolParameterAttributes);
        }
        protocol.setAttributes(attributes);
        return protocol;
    }
}
