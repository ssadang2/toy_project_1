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
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.repository.KtxRepository;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KtxServiceTest {

    @Autowired
    KtxService ktxService;

    @Autowired
    KtxRepository ktxRepository;

    @Autowired
    MugunghwaService mugunghwaService;

    @Autowired
    SaemaulService saemaulService;

    @Autowired
    DeployService deployService;

    @Test
    @Rollback(value = false)
    public void save() {
        Optional<Deploy> deploy = deployService.findDeploy(Long.valueOf(9));
        Deploy deploy1 = deploy.get();
        List<Reservation> reservations = deploy1.getReservations();
        System.out.println("reservations = " + reservations.getClass());
//        System.out.println("reservations = " + reservations);
    }
}