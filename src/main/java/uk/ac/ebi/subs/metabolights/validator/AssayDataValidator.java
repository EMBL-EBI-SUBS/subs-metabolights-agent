package uk.ac.ebi.subs.metabolights.validator;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.component.File;
import uk.ac.ebi.subs.data.submittable.Protocol;
import uk.ac.ebi.subs.validator.data.AssayDataValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssayDataValidator {

    public static final Logger logger = LoggerFactory.getLogger(AssayDataValidator.class);

    public List<SingleValidationResult> validate(AssayDataValidationMessageEnvelope envelope) {
        List<SingleValidationResult> validationResults = new ArrayList<>();
        List<File> files = envelope.getEntityToValidate().getFiles();
        List<Submittable<Protocol>> protocols = envelope.getProtocols();

        validationResults.addAll(validateMAF(files, protocols));
        validationResults.addAll(hasMafThenValidateCorrespondingProtocol(files,protocols));

        return validationResults;
    }

    public List<SingleValidationResult> validateMAF(List<File> files, List<Submittable<Protocol>> protocols) {
        List<SingleValidationResult> validationResults = new ArrayList<>();
        if (metaboliteIdentificationProtocolPresent(protocols)) {
            File mafFile = getMAFFile(files);
            if (mafFile == null) {
                validationResults.add(ValidationUtils.generateSingleValidationResult("Metabolite identification protocol is given " +
                                "but no Metabolite Annotation File (MAF) is present.",
                        SingleValidationResultStatus.Error));
            } else {
                validationResults.addAll(hasValidFileNamePattern(mafFile));
                //todo validate content
            }
        }
        return validationResults;
    }

    public boolean metaboliteIdentificationProtocolPresent(List<Submittable<Protocol>> protocols) {
        for (Submittable<Protocol> protocolSubmittable : protocols) {
            if (protocolSubmittable.getTitle() != null && !protocolSubmittable.getTitle().isEmpty()) {
                if (protocolSubmittable.getTitle().equalsIgnoreCase("Metabolite identification")) {
                    return true;
                }
            }
        }
        return false;
    }

    public File getMAFFile(List<File> files) {
        for (File file : files) {
            if (file.getType() != null && !file.getType().isEmpty()) {
                if (file.getType().equalsIgnoreCase("Metabolite Assignment File")) {
                    return file;
                }
            }
        }
        return null;
    }

    public List<SingleValidationResult> hasValidFileNamePattern(File metaboliteAnnotationFile) {
        List<SingleValidationResult> validationResults = new ArrayList<>();
        if (metaboliteAnnotationFile.getName() != null && !metaboliteAnnotationFile.getName().isEmpty()) {
            SingleValidationResult validation = isTSV(metaboliteAnnotationFile);
            if (validation != null) {
                validationResults.add(validation);
            }
            validation = hasCorrectPattern(metaboliteAnnotationFile);
            if (validation != null) {
                validationResults.add(validation);
            }
        }
        return validationResults;
    }

    public SingleValidationResult isTSV(File maf) {
        boolean isTSV = FilenameUtils.isExtension(maf.getName(), "tsv");
        if (!isTSV) {
            return ValidationUtils.generateSingleValidationResult(maf.getName() + " had invalid extension. Please provide TSV file.",
                    SingleValidationResultStatus.Error);
        }
        return null;
    }

    public SingleValidationResult hasCorrectPattern(File maf) {
        String[] split = maf.getName().split("_");
        if (split.length > 2) {
            if (!hasCorrectStart(split[0]) && !hasCorrectEnd(split[split.length - 1])) {
                return ValidationUtils.generateSingleValidationResult(maf.getName() + " has invalid file name format." +
                                " Please provide TSV file that is of format m_*_maf.tsv where " +
                                "* is substituted by your given MAFile name.",
                        SingleValidationResultStatus.Error);
            }

        } else {
            return ValidationUtils.generateSingleValidationResult(maf.getName() + " has invalid file name format." +
                            " Please provide TSV file that is of format m_*_maf.tsv where " +
                            "* is substituted by your given MAFile name.",
                    SingleValidationResultStatus.Error);
        }
        return null;
    }

    private boolean hasCorrectStart(String startString) {
        return startString.equalsIgnoreCase("m");
    }

    private boolean hasCorrectEnd(String endString) {
        return endString.equalsIgnoreCase("maf.tsv");
    }

    public List<SingleValidationResult> hasMafThenValidateCorrespondingProtocol(List<File> files, List<Submittable<Protocol>> protocols) {
        List<SingleValidationResult> validationResults = new ArrayList<>();
        File mafFile = getMAFFile(files);
        if (mafFile != null) {
            if (!metaboliteIdentificationProtocolPresent(protocols)) {
                validationResults.add(ValidationUtils.generateSingleValidationResult(mafFile.getName() + " is provided. " +
                                "But no metabolite identification protocol is present",
                        SingleValidationResultStatus.Error));
            }
        }
        return validationResults;
    }

}
