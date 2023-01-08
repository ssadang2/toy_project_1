package toy.ktx.domain.dto.api;

import lombok.Data;
import toy.ktx.domain.Member;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class MemberWithReservationDto {
    private Long memberId;

    private String name;

    private Long age;

    private List<ReservationDto2> reservationLIst;

    public MemberWithReservationDto(Member member) {
        this.memberId = member.getId();
        this.name = member.getName();
        this.age = member.getAge();
        this.reservationLIst = member.getReservations().stream().map(r -> new ReservationDto2(r)).collect(Collectors.toList());
    }

    public MemberWithReservationDto(Long memberId, String name, Long age, List<ReservationDto2> reservationLIst) {
        this.memberId = memberId;
        this.name = name;
        this.age = age;
        this.reservationLIst = reservationLIst;
    }

    public MemberWithReservationDto(Long memberId, String name, Long age) {
        this.memberId = memberId;
        this.name = name;
        this.age = age;
    }
}
