package hello.springjpa.webapp.settings.form;

import hello.springjpa.webapp.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Notifications {
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;
}