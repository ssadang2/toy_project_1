package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.ktx.KtxSeatNormal;
import toy.ktx.domain.saemaul.SaemaulSeat;
import toy.ktx.repository.KtxRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SaemaulSeatServiceTest {

    @Autowired
    SaemaulSeatService saemaulSeatService;

    @Autowired
    KtxRepository ktxRepository;

    @Autowired
    DeployService deployService;

    @Test
    void save() {
        List<Deploy> all = deployService.findAll();
        for (Deploy deploy : all) {
            System.out.println("deploy = " + deploy);
        }
    }
}