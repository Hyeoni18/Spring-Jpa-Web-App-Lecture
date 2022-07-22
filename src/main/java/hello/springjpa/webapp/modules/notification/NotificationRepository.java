package hello.springjpa.webapp.modules.notification;

import hello.springjpa.webapp.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByAccountAndChecked(Account account, boolean checked);

    @Transactional //readonly 사용 안 함. notification을 변경할거야.
    List<Notification> findByAccountAndCheckedOrderByCreatedDateTimeDesc(Account account, boolean b);

    @Transactional
    void deleteByAccountAndChecked(Account account, boolean checked);
}
