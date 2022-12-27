package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.mugunhwa.Mugunghwa;

import java.util.List;

public interface MugunghwaRepository extends JpaRepository<Mugunghwa, Long> {

    @Query("select distinct m from Mugunghwa m join fetch m.mugunghwaRooms r join fetch r.mugunghwaSeat s where m.deploy.id in :ids")
    List<Mugunghwa> getMugunghwaToSeatWithFetchAndIn(@Param("ids") List<Long> ids);
}
