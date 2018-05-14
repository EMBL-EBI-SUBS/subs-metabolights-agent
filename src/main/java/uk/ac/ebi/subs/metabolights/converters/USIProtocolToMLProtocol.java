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
public class USIProtocolToMLProtocol implements Converter<Protocol, uk.ac.ebi.subs.metabolights.model.Protocol> {
    @Override
    public uk.ac.ebi.subs.metabolights.model.Protocol convert(Protocol source) {


        uk.ac.ebi.subs.metabolights.model.Protocol protocol = new uk.ac.ebi.subs.metabolights.model.Protocol();
        protocol.setName(source.getAlias());
        protocol.setDescription(source.getDescription());
        protocol.setUri(source.getId());
        //todo URI is given for id

        Map<String, Collection<Attribute>> usiProtocolAttributes = source.getAttributes();
        // convert protocol type
        if (usiProtocolAttributes.get("protocolType") != null) {
            List<Attribute> protocolTypes = (ArrayList<Attribute>) usiProtocolAttributes.get("protocolType");
            if (protocolTypes.size() > 0) {
                Attribute attribute = protocolTypes.get(0);
                OntologyModel protocolType = new OntologyModel();
                protocolType.setAnnotationValue(attribute.getValue());
                protocol.setProtocolType(protocolType);
            }
        }
        // convert StudyProtocolParameters

        if (usiProtocolAttributes.get("parameters") != null) {
            List<Attribute> usiProtocolParameters = (ArrayList<Attribute>) usiProtocolAttributes.get("parameters");
            if (usiProtocolParameters.size() > 0) {
                List<ProtocolParameter> mlProtocolparameters = new ArrayList<>();
                for (int i = 0; i < usiProtocolAttributes.size(); i++) {
                    ProtocolParameter mlProtocolParameter = new ProtocolParameter();
                    OntologyModel mlProtocolParameterName = new OntologyModel();

                    Attribute usiAttribute = usiProtocolParameters.get(0);
                    mlProtocolParameterName.setAnnotationValue(usiAttribute.getValue());
                    mlProtocolParameter.setParameterName(mlProtocolParameterName);
                    mlProtocolparameters.add(mlProtocolParameter);
                }
                protocol.setParameters(mlProtocolparameters);
            }
        }
        return protocol;
    }
}
