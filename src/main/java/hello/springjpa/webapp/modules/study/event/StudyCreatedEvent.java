package hello.springjpa.webapp.modules.study.event;

import hello.springjpa.webapp.modules.study.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
@RequiredArgsConstructor
public class StudyCreatedEvent {

    private final Study study;

}
