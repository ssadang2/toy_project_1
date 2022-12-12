package toy.ktx.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;
import toy.ktx.domain.ktx.KtxSeatNormal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KtxSeatServiceTest {

    @Autowired
    KtxSeatService ktxSeatService;

    @Autowired
    KtxSeatNormalService ktxSeatNormalService;

    @Autowired
    KtxSeatVipService ktxSeatVipService;

    @Autowired
    KtxRoomService ktxRoomService;

    @Autowired
    DeployService deployService;

    @Autowired
    KtxService ktxService;

    @Test
    @Rollback(value = false)
    public void save() {
//        Optional<KtxRoom> ktxRoom1 = ktxRoomService.findKtxRoom(Long.valueOf(1));
//        Optional<KtxRoom> ktxRoom2 = ktxRoomService.findKtxRoom(Long.valueOf(2));
//        Optional<KtxRoom> ktxRoom3 = ktxRoomService.findKtxRoom(Long.valueOf(3));
//        Optional<KtxRoom> ktxRoom4 = ktxRoomService.findKtxRoom(Long.valueOf(4));
//        Optional<KtxRoom> ktxRoom5 = ktxRoomService.findKtxRoom(Long.valueOf(5));
//        Optional<KtxRoom> ktxRoom6 = ktxRoomService.findKtxRoom(Long.valueOf(6));
//        Optional<KtxRoom> ktxRoom7 = ktxRoomService.findKtxRoom(Long.valueOf(7));
//        Optional<KtxRoom> ktxRoom8 = ktxRoomService.findKtxRoom(Long.valueOf(8));
//        Optional<KtxRoom> ktxRoom9 = ktxRoomService.findKtxRoom(Long.valueOf(9));
//        Optional<KtxRoom> ktxRoom10 = ktxRoomService.findKtxRoom(Long.valueOf(10));

//        KtxSeat ktxSeat1 = new KtxSeat(ktxRoom1.get(), false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false);
//
//        KtxSeat ktxSeat2 = new KtxSeat(ktxRoom2.get(), false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false);
//
//        KtxSeat ktxSeat3 = new KtxSeat(ktxRoom3.get(), false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false);
//
//        KtxSeat ktxSeat4 = new KtxSeat(ktxRoom4.get(), false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false);
//
//        KtxSeat ktxSeat5 = new KtxSeat(ktxRoom5.get(), false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false);
//
//        KtxSeat ktxSeat6 = new KtxSeat(ktxRoom6.get(), false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false);
//
//        KtxSeat ktxSeat7 = new KtxSeat(ktxRoom7.get(), false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false);
//
//        KtxSeat ktxSeat8 = new KtxSeat(ktxRoom8.get(), false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false);
//
//        KtxSeat ktxSeat9 = new KtxSeat(ktxRoom9.get(), false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false);
//
//        KtxSeat ktxSeat10 = new KtxSeat(ktxRoom10.get(), false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false,
//                false, false, false, false, false, false, false, false);
//
//        ktxSeatService.saveKtxSeat(ktxSeat1);
//        ktxSeatService.saveKtxSeat(ktxSeat2);
//        ktxSeatService.saveKtxSeat(ktxSeat3);
//        ktxSeatService.saveKtxSeat(ktxSeat4);
//        ktxSeatService.saveKtxSeat(ktxSeat5);
//        ktxSeatService.saveKtxSeat(ktxSeat6);
//        ktxSeatService.saveKtxSeat(ktxSeat7);
//        ktxSeatService.saveKtxSeat(ktxSeat8);
//        ktxSeatService.saveKtxSeat(ktxSeat9);
//        ktxSeatService.saveKtxSeat(ktxSeat10);
    }

//    @Test
//    public void findByKtxRoom() {
//        Optional<KtxRoom> ktxRoom = ktxRoomService.findKtxRoom(Long.valueOf(1));
//        KtxRoom ktxRoom1 = ktxRoom.get();
//        KtxSeat ktxSeat1 = ktxRoom1.getKtxSeat();
//        System.out.println("ktxSeat1 = " + ktxSeat1.getClass());
//
//        Optional<KtxSeat> byKtxRoom = ktxSeatService.findByKtxRoom(ktxRoom1);
//        KtxSeat ktxSeat = byKtxRoom.get();
//        System.out.println("ktxSeat = " + ktxSeat.toString());
//
//        System.out.println("(ktxSeat == ktxSeat1) = " + (ktxSeat == ktxSeat1));
//
//        Assertions.assertThat(ktxSeat1).isEqualTo(ktxSeat);
//
//    }

    @Test
    public void temp() {
        Deploy deploy = deployService.getDeployWithTrain(Long.valueOf(1));
//        ktxService.findKtx(Long.valueOf(1));
    }
}
