package toy.ktx.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.controller.ScheduleController;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.QMember;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.repository.*;

import javax.persistence.EntityManager;
import java.util.*;



@SpringBootTest
@Transactional
class DeployServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    DeployService deployService;

    @Autowired
    DeployRepository deployRepository;

    @Autowired
    KtxService ktxService;

    @Autowired
    KtxRoomService ktxRoomService;

    @Autowired
    KtxRepository ktxRepository;

    @Autowired
    MugunghwaRepository mugunghwaRepository;

    @Autowired
    SaemaulRepository saemaulRepository;

    @Autowired
    KtxRoomRepository ktxRoomRepository;

    @Test
    public void saveDeploy() {
        List<Deploy> all = deployService.findAll();
        for (Deploy deploy : all) {
            System.out.println("deploy = " + deploy);
        }
    }
}