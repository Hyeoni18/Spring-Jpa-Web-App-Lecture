package hello.springjpa.webapp.event;

import hello.springjpa.webapp.domain.Account;
import hello.springjpa.webapp.domain.Enrollment;
import hello.springjpa.webapp.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);
}
