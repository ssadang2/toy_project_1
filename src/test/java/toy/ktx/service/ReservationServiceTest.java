package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.Train;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
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
    @Autowired
    DeployService deployService;
    @Autowired
    EntityManager em;

    @Test
    @Rollback(value = false)
    void saveReservation() {
        Optional<Member> member = memberService.findByLoginId("user");
        Reservation reservation = new Reservation();
        Optional<Deploy> deploy = deployService.findDeploy(Long.valueOf(5));
        reservation.setDeploy(deploy.get());
        reservation.setMember(member.get());
        reservationService.saveReservation(reservation);
    }

    @Test
    void findByMember() {
    }

    @Test
    @Rollback(value = false)
    void getReservationWithFetch() {
        reservationService.getReservationToTrainByIdWithFetch(Long.valueOf(1));
    }
}