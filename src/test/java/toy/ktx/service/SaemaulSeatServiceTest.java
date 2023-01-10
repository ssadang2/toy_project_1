package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
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

    @Test
    void save() {
        List<Long> ids = new ArrayList<>();
        ids.add(Long.valueOf(3));
        ids.add(Long.valueOf(4));
        ids.add(Long.valueOf(7));
        ids.add(Long.valueOf(12));
        ktxRepository.getKtxToSeatWithFetchAndIn(ids);
    }
}