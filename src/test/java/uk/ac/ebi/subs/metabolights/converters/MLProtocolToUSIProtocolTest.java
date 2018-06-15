package uk.ac.ebi.subs.metabolights.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.ac.ebi.subs.data.submittable.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalai on 09/02/2018.
 */
public class MLProtocolToUSIProtocolTest {
    @Test
    public void convert() throws Exception {
        MLProtocolToUSIProtocol protocolToUSIProtocol = new MLProtocolToUSIProtocol();
        List<uk.ac.ebi.subs.metabolights.model.Protocol> mlStudyProtocols = WSUtils.getMLStudyProtocols("MTBLS2");
        List<Protocol> usiProtocols = new ArrayList<>();
        for(uk.ac.ebi.subs.metabolights.model.Protocol studyProtocol : mlStudyProtocols){
            Protocol usiProtocol = protocolToUSIProtocol.convert(studyProtocol);
            usiProtocols.add(usiProtocol);
        }
        ObjectMapper mapper = new ObjectMapper();
        String usiProtocolList = mapper.writeValueAsString(usiProtocols);
        System.out.println(usiProtocolList);

    }
}
