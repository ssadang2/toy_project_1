package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.dto.api.KtxRoomWithNormalSeatDto;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.repository.KtxRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KtxRoomServiceTest {

    @Autowired
    KtxRoomService ktxRoomService;

    @Autowired
    KtxSeatNormalService ktxSeatNormalService;

    @Autowired
    KtxSeatVipService ktxSeatVipService;

    @Autowired
    KtxService ktxService;

    @Autowired
    KtxRepository ktxRepository;

    @Test
    public void save() {
        List<Ktx> temp = ktxRepository.getAllKtxToSeatFetch();
        Ktx ktx = temp.get(0);
        List<KtxRoom> ktxRooms = ktx.getKtxRooms();
        KtxRoom ktxRoom1 = ktxRooms.get(0);
        KtxRoomWithNormalSeatDto temp2 = new KtxRoomWithNormalSeatDto(ktxRoom1.getId(), ktxRoom1.getRoomName(), ktxRoom1);
        System.out.println("temp = " + temp2);
    }
}