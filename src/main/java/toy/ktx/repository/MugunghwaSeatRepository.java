package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.ktx.domain.dto.projections.MugunghwaSeatDto;
import toy.ktx.domain.mugunhwa.MugunghwaSeat;

public interface MugunghwaSeatRepository extends JpaRepository<MugunghwaSeat, Long> {

    MugunghwaSeatDto findMugunghwaSeatDtoById(Long id);
}
