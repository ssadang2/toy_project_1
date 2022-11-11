package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.ktx.domain.dto.projections.SeatDto;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;

import java.util.List;
import java.util.Optional;

public interface KtxSeatRepository extends JpaRepository<KtxSeat, Long> {

    Optional<KtxSeat> findByKtxRoom(KtxRoom ktxRoom);

    SeatDto findDtoByKtxRoom(KtxRoom ktxRoom);
}
