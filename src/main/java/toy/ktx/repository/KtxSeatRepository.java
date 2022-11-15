package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.dto.projections.VipSeatDto;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;

import java.util.Optional;

public interface KtxSeatRepository extends JpaRepository<KtxSeat, Long> {

    Optional<KtxSeat> findByKtxRoom(KtxRoom ktxRoom);

    NormalSeatDto findNormalDtoByKtxRoom(KtxRoom ktxRoom);

    VipSeatDto findVipDtoByKtxRoom(KtxRoom ktxRoom);
}
