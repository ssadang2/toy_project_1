package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
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

    @Test
    @Rollback(value = false)
    public void save() {
        Optional<Ktx> ktx001 = ktxService.findKtx(Long.valueOf(3));


        KtxRoom room1 = new KtxRoom("room1", ktx001.get(), Grade.NORMAL, ktxSeatNormalService.findById(Long.valueOf(31)).get());
        KtxRoom room2 = new KtxRoom("room2", ktx001.get(), Grade.NORMAL, ktxSeatNormalService.findById(Long.valueOf(32)).get());
        KtxRoom room3 = new KtxRoom("room3", ktx001.get(), Grade.VIP, ktxSeatVipService.findById(Long.valueOf(38)).get());
        KtxRoom room4 = new KtxRoom("room4", ktx001.get(), Grade.VIP, ktxSeatVipService.findById(Long.valueOf(39)).get());
        KtxRoom room5 = new KtxRoom("room5", ktx001.get(), Grade.VIP, ktxSeatVipService.findById(Long.valueOf(40)).get());
        KtxRoom room6 = new KtxRoom("room6", ktx001.get(), Grade.NORMAL, ktxSeatNormalService.findById(Long.valueOf(33)).get());
        KtxRoom room7 = new KtxRoom("room7", ktx001.get(), Grade.NORMAL, ktxSeatNormalService.findById(Long.valueOf(34)).get());
        KtxRoom room8 = new KtxRoom("room8", ktx001.get(), Grade.NORMAL, ktxSeatNormalService.findById(Long.valueOf(35)).get());
        KtxRoom room9 = new KtxRoom("room9", ktx001.get(), Grade.NORMAL, ktxSeatNormalService.findById(Long.valueOf(36)).get());
        KtxRoom room10 = new KtxRoom("room10", ktx001.get(), Grade.NORMAL,ktxSeatNormalService.findById(Long.valueOf(37)).get());

        ktxRoomService.saveKtxRoom(room1);
        ktxRoomService.saveKtxRoom(room2);
        ktxRoomService.saveKtxRoom(room3);
        ktxRoomService.saveKtxRoom(room4);
        ktxRoomService.saveKtxRoom(room5);
        ktxRoomService.saveKtxRoom(room6);
        ktxRoomService.saveKtxRoom(room7);
        ktxRoomService.saveKtxRoom(room8);
        ktxRoomService.saveKtxRoom(room9);
        ktxRoomService.saveKtxRoom(room10);
    }
}