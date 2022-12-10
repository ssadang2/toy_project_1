package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import toy.ktx.domain.Deploy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeployRepository extends JpaRepository<Deploy, Long> {

    @Query("select d from Deploy d where d.departurePlace = :departurePlace and d.arrivalPlace = :arrivalPlace" +
            " and d.departureTime between :departureTime and :until")
    List<Deploy> searchDeploy(@Param("departurePlace") String departurePlace, @Param("arrivalPlace") String arrivalPlace,
                                     @Param("departureTime") LocalDateTime departureTime, @Param("until") LocalDateTime until);

    @Query("select d from Deploy d join fetch d.train where d.departurePlace = :departurePlace and d.arrivalPlace = :arrivalPlace" +
            " and d.departureTime between :departureTime and :until")
    List<Deploy> searchDeployWithTrain(@Param("departurePlace") String departurePlace, @Param("arrivalPlace") String arrivalPlace,
                              @Param("departureTime") LocalDateTime departureTime, @Param("until") LocalDateTime until);

    @Query("select d from Deploy d join fetch d.train where d.id = :id")
    Deploy getDeployWithTrain(@Param("id") Long id);
}
