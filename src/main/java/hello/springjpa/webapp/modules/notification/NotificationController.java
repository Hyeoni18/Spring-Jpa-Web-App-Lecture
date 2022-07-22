package hello.springjpa.webapp.modules.notification;

import hello.springjpa.webapp.modules.account.Account;
import hello.springjpa.webapp.modules.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String getNotifications(@CurrentUser Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, false); //읽지 않은 알람
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true); //읽은 알람
        putCategorizedNotifications(model, notifications, numberOfChecked, notifications.size()); //카테고리를 나눔
        model.addAttribute("isNew", true);
        notificationService.markAsRead(notifications);
        return "notification/list";
    }

    private void putCategorizedNotifications(Model model, List<Notification> notifications,
                                             long numberOfChecked, long numberOfNotChecked) {
        List<Notification> newStudyNotifications = new ArrayList<>(); // 새로운 스터디 관련
        List<Notification> eventEnrollmentNotifications = new ArrayList<>(); // 참가신청 관련
        List<Notification> watchingStudyNotifications = new ArrayList<>(); //스터디 수정사항
        for (var notification : notifications) {
            switch (notification.getNotificationType()) {
                case STUDY_CREATED: newStudyNotifications.add(notification); break;
                case EVENT_ENROLLMENT: eventEnrollmentNotifications.add(notification); break;
                case STUDY_UPDATED: watchingStudyNotifications.add(notification); break;
            }
        }

        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("notifications", notifications);
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);
    }

    @GetMapping("/notifications/old")
    public String getOldNotifications(@CurrentUser Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, true);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);
        putCategorizedNotifications(model, notifications, notifications.size(), numberOfNotChecked);
        model.addAttribute("isNew", false);
        return "notification/list";
    }

    @DeleteMapping("/notifications")
    public String deleteNotifications(@CurrentUser Account account) {
        notificationRepository.deleteByAccountAndChecked(account, true);
        return "redirect:/notifications";
    }

}
