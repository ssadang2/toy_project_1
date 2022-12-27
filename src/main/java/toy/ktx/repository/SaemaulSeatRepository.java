package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.ktx.domain.dto.projections.SaemaulSeatDto;
import toy.ktx.domain.saemaul.SaemaulSeat;

public interface SaemaulSeatRepository extends JpaRepository<SaemaulSeat, Long> {

    SaemaulSeatDto findSaemaulSeatDtoById(Long id);
}
