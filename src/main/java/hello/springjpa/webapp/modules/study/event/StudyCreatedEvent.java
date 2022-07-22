package hello.springjpa.webapp.modules.study.event;

import hello.springjpa.webapp.modules.study.Study;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class StudyCreatedEvent {

    private Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
