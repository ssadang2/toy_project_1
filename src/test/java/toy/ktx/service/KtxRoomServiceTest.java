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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KtxRoomServiceTest {

    @Autowired
    KtxRoomService ktxRoomService;

    @Autowired
    KtxService ktxService;

    @Test
    @Rollback(value = false)
    public void save() {
        Optional<Ktx> ktx001 = ktxService.findKtx(Long.valueOf(1));

        KtxRoom room1 = new KtxRoom("room1", ktx001.get(), Grade.NORMAL);
        KtxRoom room2 = new KtxRoom("room2", ktx001.get(), Grade.NORMAL);
        KtxRoom room3 = new KtxRoom("room3", ktx001.get(), Grade.VIP);
        KtxRoom room4 = new KtxRoom("room4", ktx001.get(), Grade.NORMAL);
        KtxRoom room5 = new KtxRoom("room5", ktx001.get(), Grade.NORMAL);

        ktxRoomService.saveKtxRoom(room1);
        ktxRoomService.saveKtxRoom(room2);
        ktxRoomService.saveKtxRoom(room3);
        ktxRoomService.saveKtxRoom(room4);
        ktxRoomService.saveKtxRoom(room5);
    }
}