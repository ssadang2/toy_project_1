package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;
import toy.ktx.repository.*;

@SpringBootTest
@Transactional
class SaemaulRoomServiceTest {

    @Autowired
    SaemaulRepository saemaulRepository;
    @Autowired
    SaemaulRoomRepository saemaulRoomRepository;
    @Autowired
    SaemaulSeatRepository saemaulSeatRepository;

    @Test
    public void save() {

    }
}