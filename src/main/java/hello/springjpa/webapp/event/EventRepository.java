package hello.springjpa.webapp.event;

import hello.springjpa.webapp.domain.Event;
import hello.springjpa.webapp.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(value = "Event.withEnrollments", type= EntityGraph.EntityGraphType.LOAD)
    List<Event> findByStudyOrderByStartDateTime(Study study);
}
