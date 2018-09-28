package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.metabolights.model.OntologyModel;
import uk.ac.ebi.subs.metabolights.model.ProtocolParameter;

import java.util.*;

/**
 * Created by kalai on 01/02/2018.
 */
@Service
public class USIProtocolToMLProtocol implements Converter<Protocol, uk.ac.ebi.subs.metabolights.model.Protocol> {
    @Override
    public uk.ac.ebi.subs.metabolights.model.Protocol convert(Protocol source) {


        uk.ac.ebi.subs.metabolights.model.Protocol protocol = new uk.ac.ebi.subs.metabolights.model.Protocol();
        protocol.setName(source.getTitle());
        protocol.setDescription(source.getDescription() == null ? "" : source.getDescription());
        //todo URI is given for id

        //set placeholder values and not leave fields null
        protocol.setUri("");
        protocol.setVersion("");
        protocol.setComments(new ArrayList<>());
        protocol.setComponents(new ArrayList<>());
        OntologyModel protocolType = new OntologyModel();
        protocolType.setComments(new ArrayList<>());
        protocolType.setTermAccession("");

        Map<String, Collection<Attribute>> usiProtocolAttributes = source.getAttributes();
        // convert protocol type
        if (usiProtocolAttributes.get("protocolType") != null) {
            List<Attribute> protocolTypes = (List<Attribute>) usiProtocolAttributes.get("protocolType");
            if (protocolTypes.size() > 0) {
                Attribute attribute = protocolTypes.get(0);
                protocolType.setAnnotationValue(attribute.getValue());
                protocol.setProtocolType(protocolType);
            }
        } else {
            protocolType.setAnnotationValue("");
            protocol.setProtocolType(protocolType);
        }
        // convert StudyProtocolParameters

        if (usiProtocolAttributes.get("parameters") != null) {
            List<Attribute> usiProtocolParameters = (ArrayList<Attribute>) usiProtocolAttributes.get("parameters");
            if (usiProtocolParameters.size() > 0) {
                List<ProtocolParameter> mlProtocolparameters = new ArrayList<>();
                for (int i = 0; i < usiProtocolAttributes.size(); i++) {
                    ProtocolParameter mlProtocolParameter = new ProtocolParameter();
                    OntologyModel mlProtocolParameterName = new OntologyModel();
                    mlProtocolParameterName.setComments(new ArrayList<>());
                    mlProtocolParameterName.setTermAccession("");

                    Attribute usiAttribute = usiProtocolParameters.get(0);
                    mlProtocolParameterName.setAnnotationValue(usiAttribute.getValue());
                    mlProtocolParameter.setParameterName(mlProtocolParameterName);
                    mlProtocolparameters.add(mlProtocolParameter);
                }
                protocol.setParameters(mlProtocolparameters);
            }
        } else{
            protocol.setParameters(new ArrayList<>());
        }



        return protocol;
    }
}
