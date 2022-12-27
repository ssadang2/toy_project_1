package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.ktx.domain.dto.projections.KtxVipSeatDto;
import toy.ktx.domain.ktx.KtxSeatVip;

public interface KtxSeatVipRepository extends JpaRepository<KtxSeatVip, Long> {

        KtxVipSeatDto findVipDtoById(Long id);
}
