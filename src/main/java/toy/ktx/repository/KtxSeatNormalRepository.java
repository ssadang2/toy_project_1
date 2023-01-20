package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.ktx.domain.dto.projections.KtxNormalSeatDto;
import toy.ktx.domain.ktx.KtxSeatNormal;


public interface KtxSeatNormalRepository extends JpaRepository<KtxSeatNormal, Long> {

    KtxNormalSeatDto findNormalDtoById(Long id);
}
