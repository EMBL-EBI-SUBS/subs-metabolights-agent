package uk.ac.ebi.subs.metabolights.TestUtils;

import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Sample;
import uk.ac.ebi.subs.metabolights.converters.Utilities;
import uk.ac.ebi.subs.validator.data.SampleValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.model.Submittable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MesssageEnvelopeTestHelper {
    static Submission createNewSubmission(Team team) {
        Submission submssion = new Submission();
        submssion.setId(UUID.randomUUID().toString());
        submssion.setTeam(team);
        return submssion;
    }

    static Team createTeam() {
        Team team = new Team();
        team.setName(UUID.randomUUID().toString());
        return team;
    }

    static List<Sample> createSamples(Submission submission, Team team, int sampleNumber) {
        List<Sample> sampleList = new ArrayList<>(sampleNumber);
        for (int i = 0; i < sampleNumber; i++) {
            Sample sample = new Sample();
            sample.setTeam(team);
            String alias = UUID.randomUUID().toString();
            String accession = UUID.randomUUID().toString();
            sample.setAlias(alias);
            sample.setAccession(accession);
            sample.setAttributes(Utilities.generateUsiAttributes());
            sample.setTaxonId(10090L);
            sample.setReleaseDate(LocalDate.now());
            sampleList.add(sample);
        }
        return sampleList;
    }
    public static SampleValidationMessageEnvelope getSampleValidationEnvelope() {
        SampleValidationMessageEnvelope sampleValidationMessageEnvelope = new SampleValidationMessageEnvelope();
        Team team = createTeam();
        Submission submission = createNewSubmission(team);

        sampleValidationMessageEnvelope.setSubmissionId(submission.getId());
        sampleValidationMessageEnvelope.setValidationResultUUID(UUID.randomUUID().toString());

        List<Sample> samples = createSamples(submission, team, 3);
        sampleValidationMessageEnvelope.setEntityToValidate(getSubmittableSample(samples.get(0)));

        sampleValidationMessageEnvelope.setSampleList(getSubmittableSamples(samples, submission.getId()));
        return sampleValidationMessageEnvelope;
    }

    static uk.ac.ebi.subs.data.submittable.Sample getSubmittableSample(Sample sample){
        uk.ac.ebi.subs.data.submittable.Sample sample1 = new uk.ac.ebi.subs.data.submittable.Sample();
        sample1.setTeam(sample.getTeam());
        sample1.setAlias(sample.getAlias());
        sample1.setAccession(sample.getAccession());
        sample1.setAttributes(sample.getAttributes());
        sample1.setTaxonId(sample.getTaxonId());
        sample1.setReleaseDate(sample.getReleaseDate());
        return sample1;
    }

    static List<uk.ac.ebi.subs.validator.model.Submittable<uk.ac.ebi.subs.data.submittable.Sample>> getSubmittableSamples(List<Sample> samples, String submissionID) {
        List<uk.ac.ebi.subs.validator.model.Submittable<uk.ac.ebi.subs.data.submittable.Sample>> submittableSamples = new ArrayList<>();
        for (Sample sample : samples) {
            submittableSamples.add(new Submittable<Sample>(sample, submissionID));
        }
        return submittableSamples;
    }
}
