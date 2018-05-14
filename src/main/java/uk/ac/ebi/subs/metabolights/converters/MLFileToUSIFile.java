package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.File;
import uk.ac.ebi.subs.metabolights.model.MLFile;

public class MLFileToUSIFile implements Converter<MLFile, File> {
    @Override
    public File convert(MLFile source) {
        uk.ac.ebi.subs.data.component.File usiFile = new uk.ac.ebi.subs.data.component.File();
        usiFile.setType(source.getLabel());
        usiFile.setName(source.getFilename());
        return usiFile;
    }
}
