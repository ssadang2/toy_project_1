package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Deploy;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DeployServiceTest {

    @Autowired
    DeployService deployService;

    @Autowired
    KtxService ktxService;

    @Test
    @Rollback(value = false)
    public void saveDeploy() {
        Deploy deploy = new Deploy(LocalDateTime.of(2022, 10, 15, 7, 0, 0),
                LocalDateTime.of(2022, 10, 15, 7, 0, 0).plusHours(2).plusMinutes(40)
                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(1)).get());
        Deploy deploy2 = new Deploy(LocalDateTime.of(2022, 10, 15, 8, 0, 0),
                LocalDateTime.of(2022, 10, 15, 8, 0, 0).plusHours(2).plusMinutes(45)
                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(1)).get());
        Deploy deploy3 = new Deploy(LocalDateTime.of(2022, 10, 15, 9, 0, 0),
                LocalDateTime.of(2022, 10, 15, 9, 0, 0).plusHours(2).plusMinutes(43)
                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(1)).get());
        Deploy deploy4 = new Deploy(LocalDateTime.of(2022, 10, 15, 10, 0, 0),
                LocalDateTime.of(2022, 10, 15, 10, 0, 0).plusHours(2).plusMinutes(42)
                , "부산역", "서울역", ktxService.findKtx(Long.valueOf(1)).get());

        // 지금 같으 열차가 같은 시간대에 2가지 deploy가 있는 모순 => h2에서 delete하고 새로 ㅑnsert 해야 될 듯

        deployService.saveDeploy(deploy);
        deployService.saveDeploy(deploy2);
        deployService.saveDeploy(deploy3);
        deployService.saveDeploy(deploy4);
    }

}