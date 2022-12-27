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
    @Rollback(value = false)
    public void save() {
        Mugunghwa mugunghwa = mugunghwaRepository.findById(Long.valueOf(7)).get();

        MugunghwaRoom room1 = new MugunghwaRoom("room1", mugunghwa, mugunghwaSeatRepository.findById(Long.valueOf(6)).get());
        MugunghwaRoom room2 = new MugunghwaRoom("room2", mugunghwa, mugunghwaSeatRepository.findById(Long.valueOf(7)).get());
        MugunghwaRoom room3 = new MugunghwaRoom("room3", mugunghwa, mugunghwaSeatRepository.findById(Long.valueOf(8)).get());
        MugunghwaRoom room4 = new MugunghwaRoom("room4", mugunghwa, mugunghwaSeatRepository.findById(Long.valueOf(9)).get());
        MugunghwaRoom room5 = new MugunghwaRoom("room5", mugunghwa, mugunghwaSeatRepository.findById(Long.valueOf(10)).get());

        mugunghwaRoomRepository.save(room1);
        mugunghwaRoomRepository.save(room2);
        mugunghwaRoomRepository.save(room3);
        mugunghwaRoomRepository.save(room4);
        mugunghwaRoomRepository.save(room5);

    }
}