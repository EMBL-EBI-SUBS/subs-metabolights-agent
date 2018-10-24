package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

import java.util.List;

/**
 * Created by kalai on 24/10/2018.
 */
@Data
public class StudyFiles {
    private String obfuscation_code;
    private List<StudyFile> studyFiles;
    private List<StudyFile> upload;
    private String upload_location;
}
