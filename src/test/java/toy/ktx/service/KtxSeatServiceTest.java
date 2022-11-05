package toy.ktx.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KtxSeatServiceTest {

    @Autowired
    KtxSeatService ktxSeatService;

    @Autowired
    KtxRoomService ktxRoomService;

    @Test
    @Rollback(value = false)
    public void save() {
        Optional<KtxRoom> ktxRoom1 = ktxRoomService.findKtxRoom(Long.valueOf(1));
        Optional<KtxRoom> ktxRoom2 = ktxRoomService.findKtxRoom(Long.valueOf(2));
        Optional<KtxRoom> ktxRoom3 = ktxRoomService.findKtxRoom(Long.valueOf(3));
        Optional<KtxRoom> ktxRoom4 = ktxRoomService.findKtxRoom(Long.valueOf(4));
        Optional<KtxRoom> ktxRoom5 = ktxRoomService.findKtxRoom(Long.valueOf(5));

        KtxSeat ktxSeat1 = new KtxSeat(ktxRoom1.get(), false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);

        KtxSeat ktxSeat2 = new KtxSeat(ktxRoom2.get(), false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);

        KtxSeat ktxSeat3 = new KtxSeat(ktxRoom3.get(), false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);

        KtxSeat ktxSeat4 = new KtxSeat(ktxRoom4.get(), false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);

        KtxSeat ktxSeat5 = new KtxSeat(ktxRoom5.get(), false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false);

        ktxSeatService.saveKtxSeat(ktxSeat1);
        ktxSeatService.saveKtxSeat(ktxSeat2);
        ktxSeatService.saveKtxSeat(ktxSeat3);
        ktxSeatService.saveKtxSeat(ktxSeat4);
        ktxSeatService.saveKtxSeat(ktxSeat5);
    }

    @Test
    public void findByKtxRoom() {
        Optional<KtxRoom> ktxRoom = ktxRoomService.findKtxRoom(Long.valueOf(1));
        KtxRoom ktxRoom1 = ktxRoom.get();
        KtxSeat ktxSeat1 = ktxRoom1.getKtxSeat();
        System.out.println("ktxSeat1 = " + ktxSeat1.getClass());

        Optional<KtxSeat> byKtxRoom = ktxSeatService.findByKtxRoom(ktxRoom1);
        KtxSeat ktxSeat = byKtxRoom.get();
        System.out.println("ktxSeat = " + ktxSeat.toString());

        System.out.println("(ktxSeat == ktxSeat1) = " + (ktxSeat == ktxSeat1));

        Assertions.assertThat(ktxSeat1).isEqualTo(ktxSeat);

    }
}
