package uk.ac.ebi.subs.metabolights.model;

import lombok.Data;

/**
 * Created by kalai on 17/08/2018.
 */
@Data
public class Investigation {
    private Object validation;
    private Project isaInvestigation;
    private StudyStatus mtblsStudy;
}
