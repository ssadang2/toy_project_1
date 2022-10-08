package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.ktx.Ktx;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KtxServiceTest {

    @Autowired
    KtxService ktxService;

    @Test
    @Rollback(value = false)
    public void save() {
        Ktx test1 = new Ktx("KTX001");
        Ktx test2 = new Ktx("KTX002");
        ktxService.saveKtx(test1);
        ktxService.saveKtx(test2);

    }

}