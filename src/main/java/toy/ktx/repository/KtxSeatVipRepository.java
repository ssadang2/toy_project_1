package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.dto.projections.VipSeatDto;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeatNormal;
import toy.ktx.domain.ktx.KtxSeatVip;

import java.util.List;

public interface KtxSeatVipRepository extends JpaRepository<KtxSeatVip, Long> {

        VipSeatDto findVipDtoById(Long id);

//        @Query("select kv from KtxSeatVip kv join fetch kv.ktxRoom r join fetch r.ktx ktx join fetch ktx.deploy d where d.id = :id")
//        List<KtxSeatVip> findKtxSeatVipWithDeployIdFetch(@Param("id") Long id);
}
