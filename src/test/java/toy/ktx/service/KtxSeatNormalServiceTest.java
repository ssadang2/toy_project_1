package toy.ktx.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;
import toy.ktx.domain.ktx.KtxSeatNormal;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KtxSeatNormalServiceTest {

    @Autowired
    KtxSeatNormalService ktxSeatNormalService;

    @Autowired
    KtxRoomService ktxRoomService;

    @Test
    @Rollback(value = false)
    void save() {
//        Optional<KtxRoom> ktxRoom1 = ktxRoomService.findKtxRoom(Long.valueOf(1));
//        Optional<KtxRoom> ktxRoom2 = ktxRoomService.findKtxRoom(Long.valueOf(2));
//        Optional<KtxRoom> ktxRoom6 = ktxRoomService.findKtxRoom(Long.valueOf(6));
//        Optional<KtxRoom> ktxRoom7 = ktxRoomService.findKtxRoom(Long.valueOf(7));
//        Optional<KtxRoom> ktxRoom8 = ktxRoomService.findKtxRoom(Long.valueOf(8));
//        Optional<KtxRoom> ktxRoom9 = ktxRoomService.findKtxRoom(Long.valueOf(9));
//        Optional<KtxRoom> ktxRoom10 = ktxRoomService.findKtxRoom(Long.valueOf(10));

        KtxSeatNormal ktxSeat1 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);

        KtxSeatNormal ktxSeat2 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);


        KtxSeatNormal ktxSeat6 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);

        KtxSeatNormal ktxSeat7 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);

        KtxSeatNormal ktxSeat8 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);

        KtxSeatNormal ktxSeat9 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);

        KtxSeatNormal ktxSeat10 = new KtxSeatNormal( false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);

        ktxSeatNormalService.save(ktxSeat1);
        ktxSeatNormalService.save(ktxSeat2);
        ktxSeatNormalService.save(ktxSeat6);
        ktxSeatNormalService.save(ktxSeat7);
        ktxSeatNormalService.save(ktxSeat8);
        ktxSeatNormalService.save(ktxSeat9);
        ktxSeatNormalService.save(ktxSeat10);
    }
}