package uk.ac.ebi.subs.metabolights.converters;

import uk.ac.ebi.subs.data.component.Attribute;
import uk.ac.ebi.subs.metabolights.model.Comment;
import uk.ac.ebi.subs.metabolights.model.SampleSpreadSheetConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ConverterUtils {


    public static List<Comment> convertIndexInfoToComments(Map<String, Collection<Attribute>> usiAttributes) {
        List<Comment> comments = new ArrayList<>();
        for (Map.Entry<String, Collection<Attribute>> entry : usiAttributes.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(SampleSpreadSheetConstants.ROW_INDEX)) {
                if (entry.getValue().size() > 0) {
                    Attribute attribute = entry.getValue().iterator().next();
                    Comment comment = new Comment();
                    comment.setName(SampleSpreadSheetConstants.ROW_INDEX);
                    comment.setValue(attribute.getValue());
                    comments.add(comment);
                }
            }

        }
        return comments;
    }

    public static String extractIndexInfo(Map<String, Collection<Attribute>> usiAttributes) {
        for (Map.Entry<String, Collection<Attribute>> entry : usiAttributes.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(SampleSpreadSheetConstants.ROW_INDEX)) {
                if (entry.getValue().size() > 0) {
                    Attribute attribute = entry.getValue().iterator().next();
                    return attribute.getValue();
                }
            }

        }
        return "";
    }
}
