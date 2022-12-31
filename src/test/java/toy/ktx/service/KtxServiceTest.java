package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Member;
import toy.ktx.domain.Train;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.saemaul.Saemaul;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KtxServiceTest {

    @Autowired
    KtxService ktxService;

    @Autowired
    MugunghwaService mugunghwaService;

    @Autowired
    SaemaulService saemaulService;

    @Test
    @Rollback(value = false)
    public void save() {
        Ktx test1 = new Ktx("KTX001");
        Ktx test2 = new Ktx("KTX002");
        Ktx test3 = new Ktx("KTX003");
        Ktx test4 = new Ktx("KTX004");
        Ktx test5 = new Ktx("KTX005");

//        Mugunghwa test1 = new Mugunghwa("MUGUNGHWA001");
//        Mugunghwa test2 = new Mugunghwa("MUGUNGHWA002");
//        Mugunghwa test3 = new Mugunghwa("MUGUNGHWA003");
//        Mugunghwa test4 = new Mugunghwa("MUGUNGHWA004");
//        Mugunghwa test5 = new Mugunghwa("MUGUNGHWA005");

//        Saemaul test1 = new Saemaul("SAEMAUL001");
//        Saemaul test2 = new Saemaul("SAEMAUL002");
//        Saemaul test3 = new Saemaul("SAEMAUL003");
//        Saemaul test4 = new Saemaul("SAEMAUL004");
//        Saemaul test5 = new Saemaul("SAEMAUL005");

//        mugunghwaService.save(test1);
//        mugunghwaService.save(test2);
//        mugunghwaService.save(test3);
//        mugunghwaService.save(test4);
//        mugunghwaService.save(test5);

//        saemaulService.save(test1);
//        saemaulService.save(test2);
//        saemaulService.save(test3);
//        saemaulService.save(test4);
//        saemaulService.save(test5);

        ktxService.saveKtx(test1);
        ktxService.saveKtx(test2);
        ktxService.saveKtx(test3);
        ktxService.saveKtx(test4);
        ktxService.saveKtx(test5);
    }
}