package uk.ac.ebi.subs.metabolights.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.data.component.File;
import uk.ac.ebi.subs.metabolights.model.MLFile;

public class USIFileToMLFile implements Converter<File, MLFile> {
    @Override
    public MLFile convert(File source) {
        MLFile file = new MLFile();
        file.setFilename(source.getName());
        file.setLabel(source.getType());
        return file;
    }
}
