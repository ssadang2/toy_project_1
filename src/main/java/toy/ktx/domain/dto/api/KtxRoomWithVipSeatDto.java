package toy.ktx.domain.dto.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeatVip;

@Getter @Setter @ToString
public class KtxRoomWithVipSeatDto extends KtxRoomWithSeatDto{

    private  Boolean k1A;
    private  Boolean k1B;
    private  Boolean k1C;

    private  Boolean k2A;
    private  Boolean k2B;
    private  Boolean k2C;

    private  Boolean k3A;
    private  Boolean k3B;
    private  Boolean k3C;

    private  Boolean k4A;
    private  Boolean k4B;
    private  Boolean k4C;

    private  Boolean k5A;
    private  Boolean k5B;
    private  Boolean k5C;

    private  Boolean k6A;
    private  Boolean k6B;
    private  Boolean k6C;

    private  Boolean k7A;
    private  Boolean k7B;
    private  Boolean k7C;

    private  Boolean k8A;
    private  Boolean k8B;
    private  Boolean k8C;

    private  Boolean k9A;
    private  Boolean k9B;
    private  Boolean k9C;

    private  Boolean k10A;
    private  Boolean k10B;
    private  Boolean k10C;

    private  Boolean k11A;
    private  Boolean k11B;
    private  Boolean k11C;

    private  Boolean k12A;
    private  Boolean k12B;
    private  Boolean k12C;

    private  Boolean k13A;
    private  Boolean k13B;
    private  Boolean k13C;

    private  Boolean k14A;
    private  Boolean k14B;
    private  Boolean k14C;

    public KtxRoomWithVipSeatDto(Long roomId, String roomName, KtxRoom ktxRoom) {
        super(roomId, roomName);
        this.k1A = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK1A();
        this.k1B = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK1B();
        this.k1C = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK1C();
        this.k2A = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK2A();
        this.k2B = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK2B();
        this.k2C = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK2C();
        this.k3A = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK3A();
        this.k3B = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK3B();
        this.k3C = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK3C();
        this.k4A = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK4A();
        this.k4B = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK4B();
        this.k4C = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK4C();
        this.k5A = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK5A();
        this.k5B = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK5B();
        this.k5C = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK5C();
        this.k6A = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK6A();
        this.k6B = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK6B();
        this.k6C = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK6C();
        this.k7A = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK7A();
        this.k7B = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK7B();
        this.k7C = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK7C();
        this.k8A = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK8A();
        this.k8B = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK8B();
        this.k8C = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK8C();
        this.k9A = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK9A();
        this.k9B = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK9B();
        this.k9C = ((KtxSeatVip) ktxRoom.getKtxSeat()).getK9C();
        this.k10A =((KtxSeatVip) ktxRoom.getKtxSeat()).getK10A();
        this.k10B =((KtxSeatVip) ktxRoom.getKtxSeat()).getK10B();
        this.k10C =((KtxSeatVip) ktxRoom.getKtxSeat()).getK10C();
        this.k11A =((KtxSeatVip) ktxRoom.getKtxSeat()).getK11A();
        this.k11B =((KtxSeatVip) ktxRoom.getKtxSeat()).getK11B();
        this.k11C =((KtxSeatVip) ktxRoom.getKtxSeat()).getK11C();
        this.k12A =((KtxSeatVip) ktxRoom.getKtxSeat()).getK12A();
        this.k12B =((KtxSeatVip) ktxRoom.getKtxSeat()).getK12B();
        this.k12C =((KtxSeatVip) ktxRoom.getKtxSeat()).getK12C();
        this.k13A =((KtxSeatVip) ktxRoom.getKtxSeat()).getK13A();
        this.k13B =((KtxSeatVip) ktxRoom.getKtxSeat()).getK13B();
        this.k13C =((KtxSeatVip) ktxRoom.getKtxSeat()).getK13C();
        this.k14A =((KtxSeatVip) ktxRoom.getKtxSeat()).getK14A();
        this.k14B =((KtxSeatVip) ktxRoom.getKtxSeat()).getK14B();
        this.k14C =((KtxSeatVip) ktxRoom.getKtxSeat()).getK14C();
    }
}
