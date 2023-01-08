package toy.ktx.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.controller.ScheduleController;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.QMember;
import toy.ktx.domain.Reservation;
import toy.ktx.repository.DeployRepository;
import toy.ktx.repository.KtxRepository;
import toy.ktx.repository.MugunghwaRepository;
import toy.ktx.repository.SaemaulRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;



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

    @Test
    @Rollback(value = false)
    public void saveDeploy() {
        Deploy deploy = new Deploy();
        System.out.println("deploy.getClass() = " + deploy.getClass());
        System.out.println("Deploy.class.getClass() = " + Deploy.class.getClass());
    }
}