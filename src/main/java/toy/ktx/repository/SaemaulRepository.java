package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.saemaul.Saemaul;

import java.util.List;

public interface SaemaulRepository extends JpaRepository<Saemaul, Long> {

    @Query("select distinct sae from Saemaul sae join fetch sae.saemaulRooms r join fetch r.saemaulSeat s where sae.deploy.id in :ids")
    List<Saemaul> getSaemaulToSeatWithFetchAndIn(@Param("ids") List<Long> ids);

    @Query("select distinct sae from Saemaul sae join fetch sae.saemaulRooms r join fetch r.saemaulSeat")
    List<Saemaul> getAllSaemaulToSeatFetch();
}
