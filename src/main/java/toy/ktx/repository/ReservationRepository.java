package toy.ktx.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import toy.ktx.domain.Member;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.dto.api.ReservationDto;
import toy.ktx.domain.dto.api.ReservationDto2;
import toy.ktx.domain.dto.api.ReservationWithMemberDeployTrainDto;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMember(Member member);

    @Query("select distinct r from Reservation r join fetch r.deploy d join fetch d.train t where r.id = :id")
    Optional<Reservation> getReservationToTrainByIdWithFetch(@Param("id") Long reservationId);

    @Query("select new toy.ktx.domain.dto.api.ReservationDto(r.id, r.fee, r.seats, r.roomName, r.grade) from Reservation r")
    Page<ReservationDto> findAllReservationDtoBy(Pageable pageable);

    @Query("select new toy.ktx.domain.dto.api.ReservationDto2(r.id, r.member.id, r.fee, r.seats) from Reservation r where r.member.id in :ids")
    List<ReservationDto2> findAllReservationDto2ById(@Param("ids") List<Long> ids);

    //스프링 데이터를 이용하여 fetch join 시, count query를 만들지 못한다고 함 => count query를 별도로 분리하면 됨(entity graph는 작동한다고 함)
    //아마 Fetch join으로 paging할 수 없는 이유와 비슷할 듯? => 근데 여기는 manyToOne인데 왜??
    @Query(value = "select r from Reservation r join fetch r.member join fetch r.deploy d join fetch d.train",
            countQuery = "select count(r) from Reservation r")
    Page<Reservation> findAllReservationWithMemberDeployTrainFetch(Pageable pageable);

    //dto projection => pageable 잘 됨
    @Query("select new toy.ktx.domain.dto.api.ReservationWithMemberDeployTrainDto(r.id, r.fee, m.name, m.age, d.id, t.trainName) from Reservation r join r.member m join r.deploy d join d.train t")
    Page<ReservationWithMemberDeployTrainDto> findAllReservationDtoWithMemberDeployTrain(Pageable pageable);
}
