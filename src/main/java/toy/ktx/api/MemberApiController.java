package toy.ktx.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.ktx.domain.Member;
import toy.ktx.domain.dto.api.MemberWithReservationDto;
import toy.ktx.domain.dto.api.ReservationDto2;
import toy.ktx.domain.enums.Authorizations;
import toy.ktx.service.MemberService;
import toy.ktx.service.ReservationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

    private final MemberService memberService;
    private final ReservationService reservationService;

    //fetch join + no paing v3
    @GetMapping("/api/members/reservation-fetch")
    public List<MemberWithReservationDto> findAllMembersWithReservationFetch() {
        List<Member> members = memberService.findAllMembersWithReservationFetch();
        return members.stream().map(m -> new MemberWithReservationDto(m)).collect(Collectors.toList());
    }

    //batch + paging v3.1
    @GetMapping("/api/members/reservation")
    public Page<MemberWithReservationDto> findAllMembersWithReservation(Pageable pageable) {
        Page<Member> members = memberService.findAllByAuthorizations(Authorizations.USER, pageable);
        return members.map(m -> new MemberWithReservationDto(m));
    }

    //dto + paging v5
    //reservationDto memberId 없는 거, reservationDto2 memberId 있는 거
    //메모리 안에 넣고 돌려서 1+N -> 1+1로 만듦
    @GetMapping("/api/members-dto/reservation")
    public Page<MemberWithReservationDto> findAllMemberDtosWithReservation(Pageable pageable) {
        Page<MemberWithReservationDto> memberDtos = memberService.findAllMemberDtosByAuthorizations(Authorizations.USER, pageable);
        List<Long> memberIds = memberDtos.stream().map(m -> m.getMemberId()).collect(Collectors.toList());
        List<ReservationDto2> reservationDtos2 = reservationService.findAllReservationDto2ById(memberIds);

        Map<Long, List<ReservationDto2>> collect = reservationDtos2.stream().collect(Collectors.groupingBy(reservationDto2 -> reservationDto2.getMemberId()));
        memberDtos.forEach(m -> m.setReservationLIst(collect.get(m.getMemberId())));

        return memberDtos;
    }
}
