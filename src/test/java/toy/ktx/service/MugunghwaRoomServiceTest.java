package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.repository.MugunghwaRepository;
import toy.ktx.repository.MugunghwaRoomRepository;
import toy.ktx.repository.MugunghwaSeatRepository;

@SpringBootTest
@Transactional
class MugunghwaRoomServiceTest {

    @Autowired
    MugunghwaRepository mugunghwaRepository;
    @Autowired
    MugunghwaRoomRepository mugunghwaRoomRepository;
    @Autowired
    MugunghwaSeatRepository mugunghwaSeatRepository;

    @Test
    public void save() {
    }
}