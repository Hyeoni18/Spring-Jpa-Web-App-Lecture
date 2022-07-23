package hello.springjpa.webapp.modules.study.event;

import hello.springjpa.webapp.infra.config.AppProperties;
import hello.springjpa.webapp.infra.mail.EmailMessage;
import hello.springjpa.webapp.infra.mail.EmailService;
import hello.springjpa.webapp.modules.account.Account;
import hello.springjpa.webapp.modules.account.AccountPredicates;
import hello.springjpa.webapp.modules.account.AccountRepository;
import hello.springjpa.webapp.modules.notification.Notification;
import hello.springjpa.webapp.modules.notification.NotificationRepository;
import hello.springjpa.webapp.modules.notification.NotificationType;
import hello.springjpa.webapp.modules.study.Study;
import hello.springjpa.webapp.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Async
@Transactional
@Component
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine; // html로 메일 보낼 때 사용
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        // Study study = studyCreatedEvent.getStudy(); // getStudyToUpdateStatus 가져온 정보라 매니저 정보만 있음.
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        // QueryDSL 사용. (메이븐 컴파일 빌드를 해야 Q클래스를 생성, type safe 한 쿼리를 만들 때 사용.
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> {
            if (account.isStudyCreatedByEmail()) { // 알람을 mail로 받겠다고 했으면
                // 이메일 전송
                sendStudyCreatedEmail(study, account, "새로운 스터디가 생겼습니다",
                        "스터디올래, '" + study.getTitle() + "' 스터디가 생겼습니다.");
            }

            if (account.isStudyCreatedByWeb()) { // 알람을 web으로 받겠다고 했으면
                // create notification
                createNotification(study, account, study.getShortDescription(), NotificationType.STUDY_CREATED);
            }
        });
    }

    @EventListener
    public void handleStudyUpdateEvent(StudyUpdateEvent studyUpdateEvent) {
        Study study = studyRepository.findStudyWithManagersAndMembersById(studyUpdateEvent.getStudy().getId());
        Set<Account> accountSet = new HashSet<>(); //누구한테 보낼건지
        accountSet.addAll(study.getMembers());
        accountSet.addAll(study.getManagers());

        accountSet.forEach(account -> {
            if(account.isStudyCreatedByEmail()) {
                sendStudyCreatedEmail(study, account, studyUpdateEvent.getMessage(),
                        "스터디올래, '" + study.getTitle() + "' 스터디에 새소식이 있습니다.");
            }

            if(account.isStudyCreatedByWeb()) {
                createNotification(study, account, studyUpdateEvent.getMessage(), NotificationType.STUDY_UPDATED);
            }
        });
    }

    private void sendStudyCreatedEmail(Study study, Account account, String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(Study study, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle());
        notification.setLink("/study/" + study.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }
}
