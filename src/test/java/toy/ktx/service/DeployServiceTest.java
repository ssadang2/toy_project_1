package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.controller.ScheduleController;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.repository.DeployRepository;
import toy.ktx.repository.MugunghwaRepository;
import toy.ktx.repository.SaemaulRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DeployServiceTest {

    @Autowired
    DeployService deployService;

    @Autowired
    DeployRepository deployRepository;

    @Autowired
    KtxService ktxService;

    @Autowired
    MugunghwaRepository mugunghwaRepository;

    @Autowired
    SaemaulRepository saemaulRepository;

    @Test
//    @Rollback(value = false)
    public void saveDeploy() {
//        Deploy deploy = new Deploy(LocalDateTime.of(2023, 1, 20, 14, 0, 0),
//                LocalDateTime.of(2023, 1, 20, 14, 0, 0).plusHours(2).plusMinutes(40)
//                , "서울역", "부산역", saemaulRepository.findById(Long.valueOf(13)).get());
//        Deploy deploy2 = new Deploy(LocalDateTime.of(2022, 12, 30, 19, 0, 0),
//                LocalDateTime.of(2022, 12, 30, 19, 0, 0).plusHours(2).plusMinutes(45)
//                , "서울역", "부산역", mugunghwaRepository.findById(Long.valueOf(1)).get());
//        Deploy deploy3 = new Deploy(LocalDateTime.of(2022, 12, 30, 20, 0, 0),
//                LocalDateTime.of(2022, 12, 30, 20, 0, 0).plusHours(2).plusMinutes(43)
//                , "서울역", "부산역", ktxService.findKtx(Long.valueOf(3)).get());
//        Deploy deploy4 = new Deploy(LocalDateTime.of(2022, 12, 30, 21, 0, 0),
//                LocalDateTime.of(2022, 12, 30, 21, 0, 0).plusHours(2).plusMinutes(42)
//                , "서울역", "부산역", ktxService.findKtx(Long.valueOf(4)).get());
//        Deploy deploy5 = new Deploy(LocalDateTime.of(2022, 12, 30, 21, 0, 0),
//                LocalDateTime.of(2022, 12, 30, 21, 0, 0).plusHours(2).plusMinutes(42)
//                , "서울역", "부산역", ktxService.findKtx(Long.valueOf(5)).get());
//
//        Deploy deploy6 = new Deploy(LocalDateTime.of(2023, 1, 21, 7, 0, 0),
//                LocalDateTime.of(2023, 1, 21, 7, 0, 0).plusHours(2).plusMinutes(40)
//                , "부산역", "서울역", saemaulRepository.findById(Long.valueOf(14)).get());
//        Deploy deploy7 = new Deploy(LocalDateTime.of(2022, 12, 31, 12, 0, 0),
//                LocalDateTime.of(2022, 12, 31, 12, 0, 0).plusHours(2).plusMinutes(45)
//                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(4)).get());
//        Deploy deploy8 = new Deploy(LocalDateTime.of(2022, 12, 31, 13, 0, 0),
//                LocalDateTime.of(2022, 12, 31, 13, 0, 0).plusHours(2).plusMinutes(43)
//                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(8)).get());
//        Deploy deploy9 = new Deploy(LocalDateTime.of(2022, 12, 31, 14, 0, 0),
//                LocalDateTime.of(2022, 12, 31, 14, 0, 0).plusHours(2).plusMinutes(42)
//                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(9)).get());
//        Deploy deploy10 = new Deploy(LocalDateTime.of(2022, 12, 31, 14, 0, 0),
//                LocalDateTime.of(2022, 12, 31, 14, 0, 0).plusHours(2).plusMinutes(42)
//                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(10)).get());

//        deployService.saveDeploy(deploy);
//        deployService.saveDeploy(deploy2);
//        deployService.saveDeploy(deploy3);
//        deployService.saveDeploy(deploy4);
//        deployService.saveDeploy(deploy5);

//        deployService.saveDeploy(deploy6);
//        deployService.saveDeploy(deploy2);
//        deployService.saveDeploy(deploy3);
//        deployService.saveDeploy(deploy4);
//        deployService.saveDeploy(deploy5);

        Member member = new Member("1", "1");
        Member member2 = new Member("2", "2");
        Member member3 = new Member("1", "5");
        Member member4 = new Member("2", "4");
        Member member5 = new Member("5", "3");

        List<Member> test = new ArrayList<>();
        test.add(member);
        test.add(member2);
        test.add(member3);
        test.add(member4);
        test.add(member5);

        System.out.println("test = " + test);

        Collections.sort(test, new DeployComparator());

        System.out.println("test = " + test);
    }

    public final static class DeployComparator implements Comparator<Member> {
        @Override
        public int compare(Member member1, Member member2) {
            if (Long.valueOf(member1.getLoginId()) < Long.valueOf(member2.getLoginId())) {
                return 1;
            }
            else if (Long.valueOf(member1.getLoginId()) > Long.valueOf(member2.getLoginId())) {
                return -1;
            }
            else {
                if (Long.valueOf(member1.getPassword()) < Long.valueOf(member2.getPassword())) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }
}