package hello.springjpa.webapp.event;

import hello.springjpa.webapp.account.CurrentUser;
import hello.springjpa.webapp.domain.Account;
import hello.springjpa.webapp.domain.Study;
import hello.springjpa.webapp.event.form.EventForm;
import hello.springjpa.webapp.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;

    @GetMapping("/new-event")
    public String newEventFrom(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path); //매니저 정보만 필요
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(new EventForm());
        return "event/form";
    }
}
