package hello.springjpa.webapp.zone;

import hello.springjpa.webapp.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Zone findByCityAndProvince(String cityName, String provinceName);
}
