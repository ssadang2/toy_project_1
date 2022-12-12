package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Member;
import toy.ktx.domain.ktx.Ktx;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KtxServiceTest {

    @Autowired
    KtxService ktxService;

    @Autowired
    EntityManager em;

    @Test
    @Rollback(value = false)
    public void save() {
        Ktx test1 = new Ktx("KTX001");
        Ktx test2 = new Ktx("KTX002");
        Ktx test3 = new Ktx("KTX003");
        Ktx test4 = new Ktx("KTX004");
        Ktx test5 = new Ktx("KTX005");

        ktxService.saveKtx(test1);
        ktxService.saveKtx(test2);
        ktxService.saveKtx(test3);
        ktxService.saveKtx(test4);
        ktxService.saveKtx(test5);
    }
}