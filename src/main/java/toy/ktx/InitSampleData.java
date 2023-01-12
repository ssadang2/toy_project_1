package toy.ktx;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.Passenger;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.enums.Authorizations;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeatNormal;
import toy.ktx.domain.ktx.KtxSeatVip;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.domain.mugunhwa.MugunghwaSeat;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;
import toy.ktx.domain.saemaul.SaemaulSeat;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitSampleData {
    private final InitSampleDataService initSampleDataService;

    @PostConstruct
    public void init() {
        initSampleDataService.init();
    }

    @Component
    public static class InitSampleDataService {
        @Autowired
        private EntityManager em;

        @Transactional
        public void init() {
            //admin and user initialization
            Member admin = new Member("admin", "1234", "nam", Long.valueOf(27), Authorizations.ADMIN);

            Member user1 = new Member("user1", "1234", "kim", Long.valueOf(20), Authorizations.USER);
            Member user2 = new Member("user2", "1234", "park", Long.valueOf(30), Authorizations.USER);
            Member user3 = new Member("user3", "1234", "lee", Long.valueOf(40), Authorizations.USER);
            Member user4 = new Member("user4", "1234", "son", Long.valueOf(40), Authorizations.USER);
            Member user5 = new Member("user5", "1234", "kim", Long.valueOf(25), Authorizations.USER);

            em.persist(admin);

            em.persist(user1);
            em.persist(user2);
            em.persist(user3);
            em.persist(user4);
            em.persist(user5);

            //deploy and train and train_room and train_seat initialization(going)
            //deploy1 train(ktx) initialization
            Ktx ktx001 = getKtx("KTX001");

            //deploy1 initialization
            Deploy deploy1 = new Deploy();
            deploy1.setDeparturePlace("서울역");
            deploy1.setArrivalPlace("부산역");
            LocalDateTime deploy1Time = LocalDateTime.now().plusDays(7).plusHours(1);
            deploy1.setDepartureTime(deploy1Time);
            deploy1.setArrivalTime(deploy1Time.plusHours(2).plusMinutes(45));

            deploy1.setTrain(ktx001);
            em.persist(deploy1);

            //deploy2 train(ktx) initialization
            Ktx ktx002 = getKtx("KTX002");

            //deploy2 initialization
            Deploy deploy2 = new Deploy();
            deploy2.setDeparturePlace("서울역");
            deploy2.setArrivalPlace("부산역");
            LocalDateTime deploy2Time = LocalDateTime.now().plusDays(7).plusHours(2);
            deploy2.setDepartureTime(deploy2Time);
            deploy2.setArrivalTime(deploy2Time.plusHours(2).plusMinutes(45));

            deploy2.setTrain(ktx002);
            em.persist(deploy2);

            //deploy3 train(mugunghwa) initialization
            Mugunghwa mugunghwa001 = getMugunghwa("MUGUNGHWA001");

            //deploy3 initialization
            Deploy deploy3 = new Deploy();
            deploy3.setDeparturePlace("서울역");
            deploy3.setArrivalPlace("부산역");
            LocalDateTime deploy3Time = LocalDateTime.now().plusDays(7).plusHours(5);
            deploy3.setDepartureTime(deploy3Time);
            deploy3.setArrivalTime(deploy3Time.plusHours(4).plusMinutes(45));

            deploy3.setTrain(mugunghwa001);
            em.persist(deploy3);

            //deploy4 train(mugunghwa) initialization
            Mugunghwa mugunghwa002 = getMugunghwa("MUGUNGHWA002");

            //deploy4 initialization
            Deploy deploy4 = new Deploy();
            deploy4.setDeparturePlace("서울역");
            deploy4.setArrivalPlace("부산역");
            LocalDateTime deploy4Time = LocalDateTime.now().plusDays(7).plusHours(6);
            deploy4.setDepartureTime(deploy4Time);
            deploy4.setArrivalTime(deploy4Time.plusHours(4).plusMinutes(45));

            deploy4.setTrain(mugunghwa002);
            em.persist(deploy4);

            //deploy5 train(saemual) initialization
            Saemaul saemaul001 = getSaemaul("SAEMAUL001");

            //deploy5 initialization
            Deploy deploy5 = new Deploy();
            deploy5.setDeparturePlace("서울역");
            deploy5.setArrivalPlace("부산역");
            LocalDateTime deploy5Time = LocalDateTime.now().plusDays(7).plusHours(3);
            deploy5.setDepartureTime(deploy5Time);
            deploy5.setArrivalTime(deploy5Time.plusHours(3).plusMinutes(45));

            deploy5.setTrain(saemaul001);
            em.persist(deploy5);

            //deploy6 train(saemual) initialization
            Saemaul saemaul002 = getSaemaul("SAEMAUL002");

            //deploy6 initialization
            Deploy deploy6 = new Deploy();
            deploy6.setDeparturePlace("서울역");
            deploy6.setArrivalPlace("부산역");
            LocalDateTime deploy6Time = LocalDateTime.now().plusDays(7).plusHours(4);
            deploy6.setDepartureTime(deploy6Time);
            deploy6.setArrivalTime(deploy6Time.plusHours(3).plusMinutes(45));

            deploy6.setTrain(saemaul002);
            em.persist(deploy6);

            //deploy and train and train_room and train_seat initialization(coming)
            //deploy7 train(ktx) initialization
            Ktx ktx003 = getKtx("KTX003");

            //deploy7 initialization
            Deploy deploy7 = new Deploy();
            deploy7.setDeparturePlace("부산역");
            deploy7.setArrivalPlace("서울역");
            LocalDateTime deploy7Time = LocalDateTime.now().plusDays(10).plusHours(5);
            deploy7.setDepartureTime(deploy7Time);
            deploy7.setArrivalTime(deploy7Time.plusHours(2).plusMinutes(45));

            deploy7.setTrain(ktx003);
            em.persist(deploy7);

            //deploy8 train(ktx) initialization
            Ktx ktx004 = getKtx("KTX004");

            //deploy8 initialization
            Deploy deploy8 = new Deploy();
            deploy8.setDeparturePlace("부산역");
            deploy8.setArrivalPlace("서울역");
            LocalDateTime deploy8Time = LocalDateTime.now().plusDays(10).plusHours(3);
            deploy8.setDepartureTime(deploy8Time);
            deploy8.setArrivalTime(deploy8Time.plusHours(2).plusMinutes(45));

            deploy8.setTrain(ktx004);
            em.persist(deploy8);

            //deploy9 train(mugunghwa) initialization
            Mugunghwa mugunghwa003 = getMugunghwa("MUGUNGHWA003");

            //deploy9 initialization
            Deploy deploy9 = new Deploy();
            deploy9.setDeparturePlace("부산역");
            deploy9.setArrivalPlace("서울역");
            LocalDateTime deploy9Time = LocalDateTime.now().plusDays(10).plusHours(4);
            deploy9.setDepartureTime(deploy9Time);
            deploy9.setArrivalTime(deploy9Time.plusHours(4).plusMinutes(45));

            deploy9.setTrain(mugunghwa003);
            em.persist(deploy9);

            //deploy10 train(mugunghwa) initialization
            Mugunghwa mugunghwa004 = getMugunghwa("MUGUNGHWA004");

            //deploy10 initialization
            Deploy deploy10 = new Deploy();
            deploy10.setDeparturePlace("부산역");
            deploy10.setArrivalPlace("서울역");
            LocalDateTime deploy10Time = LocalDateTime.now().plusDays(10).plusHours(2);
            deploy10.setDepartureTime(deploy10Time);
            deploy10.setArrivalTime(deploy10Time.plusHours(4).plusMinutes(45));

            deploy10.setTrain(mugunghwa004);
            em.persist(deploy10);

            //deploy11 train(saemual) initialization
            Saemaul saemaul003 = getSaemaul("SAEMAUL003");

            //deploy11 initialization
            Deploy deploy11 = new Deploy();
            deploy11.setDeparturePlace("부산역");
            deploy11.setArrivalPlace("서울역");
            LocalDateTime deploy11Time = LocalDateTime.now().plusDays(10).plusHours(1);
            deploy11.setDepartureTime(deploy11Time);
            deploy11.setArrivalTime(deploy11Time.plusHours(3).plusMinutes(45));

            deploy11.setTrain(saemaul003);
            em.persist(deploy11);

            //deploy12 train(saemual) initialization
            Saemaul saemaul004 = getSaemaul("SAEMAUL004");

            //deploy12 initialization
            Deploy deploy12 = new Deploy();
            deploy12.setDeparturePlace("부산역");
            deploy12.setArrivalPlace("서울역");
            LocalDateTime deploy12Time = LocalDateTime.now().plusDays(10).plusHours(6);
            deploy12.setDepartureTime(deploy12Time);
            deploy12.setArrivalTime(deploy12Time.plusHours(3).plusMinutes(45));

            deploy12.setTrain(saemaul004);
            em.persist(deploy12);

            //sample reservations initialization(by user1)
            //sample reservation1
            Reservation reservation1 = new Reservation();
            reservation1.setRoomName("room1");
            reservation1.setGrade(Grade.NORMAL);
            reservation1.setSeats("k1A k1B");
            ((KtxSeatNormal)ktx001.getKtxRooms().get(0).getKtxSeat()).setK1A(true);
            ((KtxSeatNormal)ktx001.getKtxRooms().get(0).getKtxSeat()).setK1B(true);
            reservation1.setDeploy(deploy1);

            Passenger passenger1 = new Passenger();
            passenger1.setToddler(0);
            passenger1.setKids(0);
            passenger1.setAdult(2);
            passenger1.setSenior(0);

            reservation1.setMember(user1);
            reservation1.setPassenger(passenger1);
            reservation1.setFee(Long.valueOf(40000));

            em.persist(reservation1);

            //sample reservation2
            Reservation reservation2 = new Reservation();
            reservation2.setRoomName("room2");
            reservation2.setGrade(Grade.NORMAL);
            reservation2.setSeats("k2A k2B");
            ((KtxSeatNormal)ktx003.getKtxRooms().get(1).getKtxSeat()).setK2A(true);
            ((KtxSeatNormal)ktx003.getKtxRooms().get(1).getKtxSeat()).setK2B(true);
            reservation2.setDeploy(deploy7);

            Passenger passenger2 = new Passenger();
            passenger2.setToddler(0);
            passenger2.setKids(0);
            passenger2.setAdult(1);
            passenger2.setSenior(1);

            reservation2.setMember(user1);
            reservation2.setPassenger(passenger2);
            reservation2.setFee(Long.valueOf(34000));

            em.persist(reservation2);

            //sample reservation3
            Reservation reservation3 = new Reservation();
            reservation3.setRoomName("room3");
            reservation3.setSeats("m5 m6");
            mugunghwa001.getMugunghwaRooms().get(2).getMugunghwaSeat().setM5(true);
            mugunghwa001.getMugunghwaRooms().get(2).getMugunghwaSeat().setM6(true);
            reservation3.setDeploy(deploy3);

            Passenger passenger3 = new Passenger();
            passenger3.setToddler(0);
            passenger3.setKids(0);
            passenger3.setAdult(2);
            passenger3.setSenior(0);

            reservation3.setMember(user1);
            reservation3.setPassenger(passenger3);
            reservation3.setFee(Long.valueOf(20000));

            em.persist(reservation3);

            //sample reservation4
            Reservation reservation4 = new Reservation();
            reservation4.setRoomName("room5");
            reservation4.setSeats("s3A s3B s4A s4B s5A");
            saemaul003.getSaemaulRooms().get(4).getSaemaulSeat().setS3A(true);
            saemaul003.getSaemaulRooms().get(4).getSaemaulSeat().setS3B(true);
            saemaul003.getSaemaulRooms().get(4).getSaemaulSeat().setS4A(true);
            saemaul003.getSaemaulRooms().get(4).getSaemaulSeat().setS4B(true);
            saemaul003.getSaemaulRooms().get(4).getSaemaulSeat().setS5A(true);
            reservation4.setDeploy(deploy11);

            Passenger passenger4 = new Passenger();
            passenger4.setToddler(0);
            passenger4.setKids(1);
            passenger4.setAdult(2);
            passenger4.setSenior(2);

            reservation4.setMember(user1);
            reservation4.setPassenger(passenger4);
            reservation4.setFee(Long.valueOf(58500));

            em.persist(reservation4);

            //sample reservations initialization(by user5)
            //sample reservation5
            Reservation reservation5 = new Reservation();
            reservation5.setRoomName("room3");
            reservation5.setGrade(Grade.VIP);
            reservation5.setSeats("k5C k6C");
            ((KtxSeatVip)ktx002.getKtxRooms().get(2).getKtxSeat()).setK5C(true);
            ((KtxSeatVip)ktx002.getKtxRooms().get(2).getKtxSeat()).setK6C(true);
            reservation5.setDeploy(deploy2);

            Passenger passenger5 = new Passenger();
            passenger5.setToddler(2);
            passenger5.setKids(0);
            passenger5.setAdult(2);
            passenger5.setSenior(0);

            reservation5.setMember(user5);
            reservation5.setPassenger(passenger5);
            reservation5.setFee(Long.valueOf(75000));

            em.persist(reservation5);

            //sample reservation6
            Reservation reservation6 = new Reservation();
            reservation6.setRoomName("room5");
            reservation6.setGrade(Grade.VIP);
            reservation6.setSeats("k2A k2B");
            ((KtxSeatVip)ktx004.getKtxRooms().get(4).getKtxSeat()).setK2A(true);
            ((KtxSeatVip)ktx004.getKtxRooms().get(4).getKtxSeat()).setK2B(true);
            reservation6.setDeploy(deploy8);

            Passenger passenger6 = new Passenger();
            passenger6.setToddler(0);
            passenger6.setKids(0);
            passenger6.setAdult(1);
            passenger6.setSenior(1);

            reservation6.setMember(user5);
            reservation6.setPassenger(passenger6);
            reservation6.setFee(Long.valueOf(51000));

            em.persist(reservation6);

            //sample reservation7
            Reservation reservation7 = new Reservation();
            reservation7.setRoomName("room3");
            reservation7.setSeats("m5 m6");
            mugunghwa004.getMugunghwaRooms().get(2).getMugunghwaSeat().setM5(true);
            mugunghwa004.getMugunghwaRooms().get(2).getMugunghwaSeat().setM6(true);
            reservation7.setDeploy(deploy10);

            Passenger passenger7 = new Passenger();
            passenger7.setToddler(0);
            passenger7.setKids(0);
            passenger7.setAdult(2);
            passenger7.setSenior(0);

            reservation7.setMember(user5);
            reservation7.setPassenger(passenger7);
            reservation7.setFee(Long.valueOf(20000));

            em.persist(reservation7);

            //sample reservation8
            Reservation reservation8 = new Reservation();
            reservation8.setRoomName("room3");
            reservation8.setSeats("s3A s3B s4A s4B s5A");
            saemaul001.getSaemaulRooms().get(2).getSaemaulSeat().setS3A(true);
            saemaul001.getSaemaulRooms().get(2).getSaemaulSeat().setS3B(true);
            saemaul001.getSaemaulRooms().get(2).getSaemaulSeat().setS4A(true);
            saemaul001.getSaemaulRooms().get(2).getSaemaulSeat().setS4B(true);
            saemaul001.getSaemaulRooms().get(2).getSaemaulSeat().setS5A(true);
            reservation8.setDeploy(deploy5);

            Passenger passenger8 = new Passenger();
            passenger8.setToddler(0);
            passenger8.setKids(1);
            passenger8.setAdult(2);
            passenger8.setSenior(2);

            reservation8.setMember(user5);
            reservation8.setPassenger(passenger8);
            reservation8.setFee(Long.valueOf(58500));

            em.persist(reservation8);
        }
        private Ktx getKtx(String trainName) {
            Ktx ktx = new Ktx();
            ktx.setTrainName(trainName);

            //ktxSeat initialization
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

            //ktxRoom initialization
            List<KtxRoom> rooms = new ArrayList<>();
            rooms.add(new KtxRoom("room1", ktx, Grade.NORMAL, ktxSeat1));
            rooms.add(new KtxRoom("room2", ktx, Grade.NORMAL, ktxSeat2));
            rooms.add(new KtxRoom("room3", ktx, Grade.VIP, ktxSeat3));
            rooms.add(new KtxRoom("room4", ktx, Grade.VIP, ktxSeat4));
            rooms.add(new KtxRoom("room5", ktx, Grade.VIP, ktxSeat5));
            rooms.add(new KtxRoom("room6", ktx, Grade.NORMAL, ktxSeat6));
            rooms.add(new KtxRoom("room7", ktx, Grade.NORMAL, ktxSeat7));
            rooms.add(new KtxRoom("room8", ktx, Grade.NORMAL, ktxSeat8));
            rooms.add(new KtxRoom("room9", ktx, Grade.NORMAL, ktxSeat9));
            rooms.add(new KtxRoom("room10", ktx, Grade.NORMAL,ktxSeat10));
            ktx.setKtxRooms(rooms);

            return ktx;
        }

        private Mugunghwa getMugunghwa(String trainName) {
            Mugunghwa mugunghwa = new Mugunghwa();
            mugunghwa.setTrainName(trainName);

            MugunghwaSeat seat1 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat2 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat3 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat4 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat5 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            List<MugunghwaRoom> mugunghwaRooms = new ArrayList<>();
            mugunghwaRooms.add(new MugunghwaRoom("room1", mugunghwa, seat1));
            mugunghwaRooms.add(new MugunghwaRoom("room2", mugunghwa, seat2));
            mugunghwaRooms.add(new MugunghwaRoom("room3", mugunghwa, seat3));
            mugunghwaRooms.add(new MugunghwaRoom("room4", mugunghwa, seat4));
            mugunghwaRooms.add(new MugunghwaRoom("room5", mugunghwa, seat5));
            mugunghwa.setMugunghwaRooms(mugunghwaRooms);

            return mugunghwa;
        }

        private Saemaul getSaemaul(String trainName) {
            Saemaul saemaul = new Saemaul();
            saemaul.setTrainName(trainName);

            SaemaulSeat seat1 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat2 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat3 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat4 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat5 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            List<SaemaulRoom> saemaulRooms = new ArrayList<>();
            saemaulRooms.add(new SaemaulRoom("room1", saemaul, seat1));
            saemaulRooms.add(new SaemaulRoom("room3", saemaul, seat2));
            saemaulRooms.add(new SaemaulRoom("room2", saemaul, seat3));
            saemaulRooms.add(new SaemaulRoom("room4", saemaul, seat4));
            saemaulRooms.add(new SaemaulRoom("room5", saemaul, seat5));
            saemaul.setSaemaulRooms(saemaulRooms);

            return saemaul;
        }
    }
}
