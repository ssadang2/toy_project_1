package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Deploy;
import toy.ktx.repository.MugunghwaRepository;
import toy.ktx.repository.SaemaulRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DeployServiceTest {

    @Autowired
    DeployService deployService;

    @Autowired
    KtxService ktxService;

    @Autowired
    MugunghwaRepository mugunghwaRepository;

    @Autowired
    SaemaulRepository saemaulRepository;

    @Test
    @Rollback(value = false)
    public void saveDeploy() {
//        Deploy deploy = new Deploy(LocalDateTime.of(2022, 12, 30, 16, 0, 0),
//                LocalDateTime.of(2022, 12, 30, 16, 0, 0).plusHours(2).plusMinutes(40)
//                , "서울역", "부산역", saemaulRepository.findById(Long.valueOf(11)).get());
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
        Deploy deploy = new Deploy(LocalDateTime.of(2022, 12, 31, 9, 0, 0),
                LocalDateTime.of(2022, 12, 31, 9, 0, 0).plusHours(2).plusMinutes(40)
                , "부산역", "서울역", saemaulRepository.findById(Long.valueOf(12)).get());
//        Deploy deploy2 = new Deploy(LocalDateTime.of(2022, 12, 31, 12, 0, 0),
//                LocalDateTime.of(2022, 12, 31, 12, 0, 0).plusHours(2).plusMinutes(45)
//                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(4)).get());
//        Deploy deploy3 = new Deploy(LocalDateTime.of(2022, 12, 31, 13, 0, 0),
//                LocalDateTime.of(2022, 12, 31, 13, 0, 0).plusHours(2).plusMinutes(43)
//                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(8)).get());
//        Deploy deploy4 = new Deploy(LocalDateTime.of(2022, 12, 31, 14, 0, 0),
//                LocalDateTime.of(2022, 12, 31, 14, 0, 0).plusHours(2).plusMinutes(42)
//                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(9)).get());
//        Deploy deploy5 = new Deploy(LocalDateTime.of(2022, 12, 31, 14, 0, 0),
//                LocalDateTime.of(2022, 12, 31, 14, 0, 0).plusHours(2).plusMinutes(42)
//                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(10)).get());

        deployService.saveDeploy(deploy);
//        deployService.saveDeploy(deploy2);
//        deployService.saveDeploy(deploy3);
//        deployService.saveDeploy(deploy4);
//        deployService.saveDeploy(deploy5);
    }

}