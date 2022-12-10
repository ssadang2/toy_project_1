package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.dto.projections.VipSeatDto;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;

import java.util.List;
import java.util.Optional;

public interface KtxSeatRepository extends JpaRepository<KtxSeat, Long> {

    Optional<KtxSeat> findByKtxRoom(KtxRoom ktxRoom);

    NormalSeatDto findNormalDtoByKtxRoom(KtxRoom ktxRoom);

    VipSeatDto findVipDtoByKtxRoom(KtxRoom ktxRoom);

    @Query("select k from KtxSeat k join fetch k.ktxRoom r join fetch r.ktx ktx join fetch ktx.deploy d where d.id = :id")
    List<KtxSeat> findKtxSeatWithKtxRoomWithTrainWithDeploy(@Param("id") Long id);
}
