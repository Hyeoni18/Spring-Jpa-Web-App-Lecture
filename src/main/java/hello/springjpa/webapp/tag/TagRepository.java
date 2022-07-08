package hello.springjpa.webapp.tag;

import hello.springjpa.webapp.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByTitle(String title);
}
