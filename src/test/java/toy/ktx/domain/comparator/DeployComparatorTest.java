package toy.ktx.domain.comparator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.service.MemberService;
import toy.ktx.service.ReservationService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class DeployComparatorTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    MemberService memberService;

    @Test
    //1차 정렬 출발 시간 순, 2차 정렬 ktx -> 무궁화호 -> 새마을호 순
    void compare() {
        //given
        List<Deploy> deployList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        Deploy deploy1 = new Deploy(now, now.plusHours(2), "서울역", "부산역", new Mugunghwa("mugunghwa1"));
        Deploy deploy2 = new Deploy(now.plusHours(1), now.plusHours(3), "서울역", "부산역", new Mugunghwa("mugunghwa2"));
        Deploy deploy3 = new Deploy(now, now.plusHours(2), "서울역", "부산역", new Saemaul("saemaul3"));
        Deploy deploy4 = new Deploy(now.plusHours(1), now.plusHours(3), "서울역", "부산역", new Saemaul("saemaul4"));
        Deploy deploy5 = new Deploy(now, now.plusHours(2), "서울역", "부산역", new Ktx("ktx5"));
        Deploy deploy6 = new Deploy(now.plusHours(1), now.plusHours(3), "서울역", "부산역", new Ktx("ktx6"));

        deployList.add(deploy1);
        deployList.add(deploy2);
        deployList.add(deploy3);
        deployList.add(deploy4);
        deployList.add(deploy5);
        deployList.add(deploy6);

        //when
        Collections.sort(deployList, new DeployComparator());

        //thens
        System.out.println("deployList = " + deployList);
        assertThat(deployList).containsExactly(deploy5, deploy1, deploy3, deploy6, deploy2, deploy4);
    }
}