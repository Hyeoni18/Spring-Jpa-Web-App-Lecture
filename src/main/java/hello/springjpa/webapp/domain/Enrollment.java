package hello.springjpa.webapp.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne //하나의 enrollment는 하나의 이벤트에 속해있지만, 이벤트 입장에서는 참가신청이 여러개일 수 있음.
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt; //언제 신청했는지, 선착순 순서가 됨

    private boolean accepted;

    private boolean attended;

}
