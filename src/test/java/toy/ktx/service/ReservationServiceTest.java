package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Member;
import toy.ktx.domain.Reservation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;
    @Autowired
    MemberService memberService;

    @Test
    void saveReservation() {
        Optional<Member> member = memberService.findByLoginId("user");
        List<Reservation> byMember = reservationService.findByMember(member.get());
    }

    @Test
    void findByMember() {
    }
}