package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeatNormal;
import toy.ktx.domain.ktx.KtxSeatVip;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KtxSeatVipServiceTest {

    @Autowired
    KtxSeatVipService ktxSeatVipService;

    @Autowired
    KtxRoomService ktxRoomService;

    @Test
    @Rollback(value = false)
    void save() {

//        Optional<KtxRoom> ktxRoom1 = ktxRoomService.findKtxRoom(Long.valueOf(1));
//        Optional<KtxRoom> ktxRoom2 = ktxRoomService.findKtxRoom(Long.valueOf(2));
        Optional<KtxRoom> ktxRoom3 = ktxRoomService.findKtxRoom(Long.valueOf(3));
        Optional<KtxRoom> ktxRoom4 = ktxRoomService.findKtxRoom(Long.valueOf(4));
        Optional<KtxRoom> ktxRoom5 = ktxRoomService.findKtxRoom(Long.valueOf(5));
//        Optional<KtxRoom> ktxRoom6 = ktxRoomService.findKtxRoom(Long.valueOf(6));
//        Optional<KtxRoom> ktxRoom7 = ktxRoomService.findKtxRoom(Long.valueOf(7));
//        Optional<KtxRoom> ktxRoom8 = ktxRoomService.findKtxRoom(Long.valueOf(8));
//        Optional<KtxRoom> ktxRoom9 = ktxRoomService.findKtxRoom(Long.valueOf(9));
//        Optional<KtxRoom> ktxRoom10 = ktxRoomService.findKtxRoom(Long.valueOf(10));

        KtxSeatVip ktxSeat3 = new KtxSeatVip(false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false);

        KtxSeatVip ktxSeat4 = new KtxSeatVip(false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false);

        KtxSeatVip ktxSeat5 = new KtxSeatVip( false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false);

        ktxSeatVipService.save(ktxSeat3);
        ktxSeatVipService.save(ktxSeat4);
        ktxSeatVipService.save(ktxSeat5);
    }
}