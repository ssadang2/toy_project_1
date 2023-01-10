package toy.ktx.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import toy.ktx.domain.Member;
import toy.ktx.domain.dto.api.MemberWithReservationDto;
import toy.ktx.domain.enums.Authorizations;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    Page<Member> findAllByAuthorizations(Authorizations authorizations, Pageable pageable);

    @Query("select new toy.ktx.domain.dto.api.MemberWithReservationDto(m.id, m.name, m.age) from Member m where m.authorizations = :authorizations")
    Page<MemberWithReservationDto> findAllMemberDtosByAuthorizations(@Param("authorizations") Authorizations authorizations, Pageable pageable);

    @Query("select distinct m from Member m join fetch m.reservations")
    List<Member> findAllMembersWithReservationFetch();

    //dto projection pageable 사용 가능
    @Query("select new toy.ktx.domain.dto.api.MemberWithReservationDto(m.id, m.name, m.age) from Member m")
    Page<MemberWithReservationDto> findAllMemberDtos(Pageable pageable);
}
