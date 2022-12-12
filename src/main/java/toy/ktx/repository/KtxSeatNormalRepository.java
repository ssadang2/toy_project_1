package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;
import toy.ktx.domain.ktx.KtxSeatNormal;

import java.util.List;

public interface KtxSeatNormalRepository extends JpaRepository<KtxSeatNormal, Long> {

    NormalSeatDto findNormalDtoById(Long id);

//    @Query("select kn from KtxSeatNormal kn join fetch kn.ktxRoom r join fetch r.ktx ktx join fetch ktx.deploy d where d.id = :id")
//    List<KtxSeatNormal> findKtxSeatNormalWithDeployIdFetch(@Param("id") Long id);
}
