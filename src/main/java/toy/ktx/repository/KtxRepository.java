package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;

import java.util.List;

public interface KtxRepository extends JpaRepository<Ktx, Long> {

    @Query("select distinct k from Ktx k join fetch k.ktxRooms r join fetch r.ktxSeat s where k.deploy.id in :ids")
    List<Ktx> getKtxToSeatWithFetchAndIn(@Param("ids") List<Long> ids);

    @Query("select distinct k from Ktx k join fetch k.ktxRooms r join fetch r.ktxSeat")
    List<Ktx> getAllKtxToSeatFetch();

}
