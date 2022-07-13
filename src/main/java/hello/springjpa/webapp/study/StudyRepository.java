package hello.springjpa.webapp.study;

import hello.springjpa.webapp.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD) //LOAD는 엔티티 매니저에 정의한 연관관계는 EAGER 모드로 가져오기. 나머지는 기본 fatch 타입에 따른다.
    Study findByPath(String path);
}
