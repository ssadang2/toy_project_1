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
    @Rollback(value = false)
    public void save() {
        Saemaul saemaul = saemaulRepository.findById(Long.valueOf(14)).get();

        SaemaulRoom room1 = new SaemaulRoom("room1", saemaul, saemaulSeatRepository.findById(Long.valueOf(16)).get());
        SaemaulRoom room2 = new SaemaulRoom("room2", saemaul, saemaulSeatRepository.findById(Long.valueOf(17)).get());
        SaemaulRoom room3 = new SaemaulRoom("room3", saemaul, saemaulSeatRepository.findById(Long.valueOf(18)).get());
        SaemaulRoom room4 = new SaemaulRoom("room4", saemaul, saemaulSeatRepository.findById(Long.valueOf(19)).get());
        SaemaulRoom room5 = new SaemaulRoom("room5", saemaul, saemaulSeatRepository.findById(Long.valueOf(20)).get());

        saemaulRoomRepository.save(room1);
        saemaulRoomRepository.save(room2);
        saemaulRoomRepository.save(room3);
        saemaulRoomRepository.save(room4);
        saemaulRoomRepository.save(room5);

    }
}