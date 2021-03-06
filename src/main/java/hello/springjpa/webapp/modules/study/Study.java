package hello.springjpa.webapp.modules.study;

import hello.springjpa.webapp.modules.account.Account;
import hello.springjpa.webapp.modules.account.UserAccount;
import hello.springjpa.webapp.modules.tag.Tag;
import hello.springjpa.webapp.modules.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//view 변경할 때마다 발생하는 쿼리 살펴보기, 어차피 가져올 데이터라면 join해서 가져오기.
// left outer join으로 연관 데이터 한 번에 조회 가능
// entityManagerGraph 정의 -> repository에서 사용
//@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
//        @NamedAttributeNode("tags"),
//        @NamedAttributeNode("zones"),
//        @NamedAttributeNode("managers"),
//        @NamedAttributeNode("members")})
//@NamedEntityGraph(name = "Study.withTagsAndManagers", attributeNodes = {
//        @NamedAttributeNode("tags"),
//        @NamedAttributeNode("managers")})
//@NamedEntityGraph(name = "Study.withZonesAndManagers", attributeNodes = {
//        @NamedAttributeNode("zones"),
//        @NamedAttributeNode("managers")})
//@NamedEntityGraph(name = "Study.withManagers", attributeNodes = {
//        @NamedAttributeNode("managers")})
//@NamedEntityGraph(name = "Study.withTagsAndZones", attributeNodes = {
//        @NamedAttributeNode("tags"),
//        @NamedAttributeNode("zones")})
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Study {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER) // 무조건 가져오기
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    public void addManager(Account account) {
        this.managers.add(account);
    }

    //가입 가능 여부
    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting() //공개 가능, 모집 중, 멤버, 매니저가 아니면.
                && !this.members.contains(account) && !this.managers.contains(account);
    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }

    public void publish() {
        if(!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("스터디를 공개할 수 없는 상태입니다. 스터디를 이미 공개했거나 종료했습니다.");
        }
    }

    public void close() {
        if(this.published && !this.closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("스터디를 종료할 수 없습니다. 스터디를 공개하지 않았거나 이미 종료한 스터디입니다.");
        }
    }

    public void startRecruit() {
        if(canUpdateRecruiting()) {
            this.recruiting = true;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 시작할 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    public boolean canUpdateRecruiting() {
        return this.published && this.recruitingUpdatedDateTime == null || this.recruitingUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(1));
    }

    public void stopRecruit() {
        if(canUpdateRecruiting()) {
            this.recruiting = false;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 멈출 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    public boolean isRemovable() {
        return !this.published; //TODO 모임을 했던 스터디는 삭제할 수 없다.
    }

    public String getEncodedPath() {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }

    public void addMember(Account account) {
        this.getMembers().add(account);
    }

    public void removeMember(Account account) {
        this.getMembers().remove(account);
    }

    public void addMemeber(Account account) {
        this.members.add(account);
    }

    public boolean isManagedBy(Account account) {
        return this.getManagers().contains(account);
    }
}
