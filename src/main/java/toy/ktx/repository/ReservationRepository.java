package toy.ktx.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import toy.ktx.domain.Member;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.ktx.Ktx;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMember(Member member);

    @Query("select distinct r from Reservation r join fetch r.deploy d join fetch d.train t where r.id = :id")
    Optional<Reservation> getReservationToTrainByIdWithFetch(@Param("id") Long reservationId);
}
