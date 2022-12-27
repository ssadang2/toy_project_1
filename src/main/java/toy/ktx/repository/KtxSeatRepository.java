package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.ktx.domain.ktx.KtxSeat;

public interface KtxSeatRepository extends JpaRepository<KtxSeat, Long> {
}
