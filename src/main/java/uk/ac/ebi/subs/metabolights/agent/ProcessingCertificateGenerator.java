package uk.ac.ebi.subs.metabolights.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.data.submittable.Submittable;
import uk.ac.ebi.subs.processing.ProcessingCertificate;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProcessingCertificateGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ProcessingCertificateGenerator.class);

    public List<ProcessingCertificate> acknowledgeReception(List<Submittable> anySubmittableList) {
        logger.debug("Acknowledging submission reception");

        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();

        anySubmittableList.forEach(anySubmittable -> {
            ProcessingCertificate pc = new ProcessingCertificate(
                    anySubmittable,
                    Archive.Metabolights,
                    ProcessingStatusEnum.Received
            );
            processingCertificateList.add(pc);
        });

        return processingCertificateList;
    }

    public List<ProcessingCertificate> generateCertificates(List<Sample> anySubmittableList) {
        logger.debug("Generating certificates...");

        List<ProcessingCertificate> processingCertificateList = new ArrayList<>();

        anySubmittableList.forEach(anySubmittable -> {
            ProcessingCertificate pc = new ProcessingCertificate(
                    anySubmittable,
                    Archive.Metabolights,
                    ProcessingStatusEnum.Completed,
                    anySubmittable.getAccession()
            );
            processingCertificateList.add(pc);
        });

        return processingCertificateList;
    }

}
