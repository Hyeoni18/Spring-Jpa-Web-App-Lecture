package hello.springjpa.webapp.modules.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

//    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD) //LOAD는 엔티티 매니저에 정의한 연관관계는 EAGER 모드로 가져오기. 나머지는 기본 fatch 타입에 따른다.
    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

//    @EntityGraph(value = "Study.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    @EntityGraph(attributePaths = {"tags", "managers"})
    Study findStudyWithTagsByPath(String path);

//    @EntityGraph(value = "Study.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    @EntityGraph(attributePaths = {"zones", "managers"})
    Study findStudyWithZonesByPath(String path);

//    @EntityGraph(value = "Study.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    @EntityGraph(attributePaths = "managers")
    Study findStudyWithManagersByPath(String path);

    @EntityGraph(attributePaths = "members")
    Study findStudyWithMembersByPath(String path);

    Study findStudyOnlyByPath(String path);

//    @EntityGraph(value = "Study.withTagsAndZones", type = EntityGraph.EntityGraphType.FETCH)
    @EntityGraph(attributePaths = {"zones", "tags"})
    Study findStudyWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"members", "managers"}) //한 곳에서 사용할 땐 이렇게 해도 됨. value 값을 정해주는 것은 자주 사용하고 여러 곳에서 사용할 때 쓰는 방법.
    Study findStudyWithManagersAndMembersById(Long id);

}
