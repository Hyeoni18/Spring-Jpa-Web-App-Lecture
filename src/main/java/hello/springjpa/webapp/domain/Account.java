package hello.springjpa.webapp.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified; // 이메일 인증 여부

    private String emailCheckToken; // 이메일 검증 시 사용할 토큰 값

    private LocalDateTime joinedAt; // 이메일 인증 완료 시 가입 날짜 저장

    // 기본 프로필 정보
    private String bio;
    private String url;
    private String occupation;
    private String location;
    @Lob // String은 varchar로 매핑되는데 보다 길게 저장될 경우 Lob을 설정해주면 text로 매핑된다.
    @Basic(fetch = FetchType.EAGER) // 기본 타입은 LAZY 이지만, 프로필 사진을 그때그때 가져오는게 좋을거 같아서 EAGER로 설정
    private String profileImage;

    // 알림 정보
    private boolean studyCreatedByEmail; // 스터디가 만들어졌음을 메일로 받을지
    private boolean studyCreatedByWeb = true; // 스터디가 만들어졌음을 웹으로 받을지
    private boolean studyEnrollmentResultByEmail; // 스터디 가입신청 결과를 메일로 받을지
    private boolean studyEnrollmentResultByWeb = true; // 스터디 가입신청 결과를 웹으로 받을지
    private boolean studyUpdatedByEmail; // 스터디 변경사항을 메일로 받을지
    private boolean studyUpdatedByWeb = true; // 스터디 변경사항을 웹으로 받을지

    @ManyToMany
    private Set<Tag> tags = new HashSet<>(); //비어있는 컬렉션으로 세팅하는게 좋음

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime emailCheckTokenGeneratedAt; //이메일 토큰 생성 시간

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1)); //1시간 이전에 만들었다면 True
    }
}
