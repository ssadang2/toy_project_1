package toy.ktx.domain.dto.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeatNormal;

@Getter @Setter @ToString
public class KtxRoomWithNormalSeatDto extends KtxRoomWithSeatDto{

    private Boolean k1A;
    private Boolean k2A;
    private Boolean k3A;
    private Boolean k4A;
    private Boolean k5A;
    private Boolean k6A;
    private Boolean k7A;
    private Boolean k8A;
    private Boolean k9A;
    private Boolean k10A;
    private Boolean k11A;
    private Boolean k12A;
    private Boolean k13A;
    private Boolean k14A;

    private Boolean k1B;
    private Boolean k2B;
    private Boolean k3B;
    private Boolean k4B;
    private Boolean k5B;
    private Boolean k6B;
    private Boolean k7B;
    private Boolean k8B;
    private Boolean k9B;
    private Boolean k10B;
    private Boolean k11B;
    private Boolean k12B;
    private Boolean k13B;
    private Boolean k14B;

    private Boolean k1C;
    private Boolean k2C;
    private Boolean k3C;
    private Boolean k4C;
    private Boolean k5C;
    private Boolean k6C;
    private Boolean k7C;
    private Boolean k8C;
    private Boolean k9C;
    private Boolean k10C;
    private Boolean k11C;
    private Boolean k12C;
    private Boolean k13C;
    private Boolean k14C;

    private Boolean k1D;
    private Boolean k2D;
    private Boolean k3D;
    private Boolean k4D;
    private Boolean k5D;
    private Boolean k6D;
    private Boolean k7D;
    private Boolean k8D;
    private Boolean k9D;
    private Boolean k10D;
    private Boolean k11D;
    private Boolean k12D;
    private Boolean k13D;
    private Boolean k14D;

    public KtxRoomWithNormalSeatDto(Long roomId, String roomName) {
        super(roomId, roomName);
    }

    public KtxRoomWithNormalSeatDto(Long roomId, String roomName, KtxRoom ktxRoom) {
        super(roomId, roomName);
        this.k1A = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK1A();
        this.k2A = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK2A();
        this.k3A = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK3A();
        this.k4A = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK4A();
        this.k5A = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK5A();
        this.k6A = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK6A();
        this.k7A = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK7A();
        this.k8A = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK8A();
        this.k9A = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK9A();
        this.k10A =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK10A();
        this.k11A =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK11A();
        this.k12A =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK12A();
        this.k13A =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK13A();
        this.k14A =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK14A();

        this.k1B = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK1B();
        this.k2B = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK2B();
        this.k3B = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK3B();
        this.k4B = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK4B();
        this.k5B = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK5B();
        this.k6B = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK6B();
        this.k7B = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK7B();
        this.k8B = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK8B();
        this.k9B = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK9B();
        this.k10B =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK10B();
        this.k11B =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK11B();
        this.k12B =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK12B();
        this.k13B =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK13B();
        this.k14B =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK14B();

        this.k1C = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK1C();
        this.k2C = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK2C();
        this.k3C = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK3C();
        this.k4C = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK4C();
        this.k5C = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK5C();
        this.k6C = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK6C();
        this.k7C = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK7C();
        this.k8C = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK8C();
        this.k9C = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK9C();
        this.k10C =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK10C();
        this.k11C =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK11C();
        this.k12C =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK12C();
        this.k13C =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK13C();
        this.k14C =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK14C();

        this.k1D = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK1D();
        this.k2D = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK2D();
        this.k3D = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK3D();
        this.k4D = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK4D();
        this.k5D = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK5D();
        this.k6D = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK6D();
        this.k7D = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK7D();
        this.k8D = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK8D();
        this.k9D = ((KtxSeatNormal) ktxRoom.getKtxSeat()).getK9D();
        this.k10D =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK10D();
        this.k11D =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK11D();
        this.k12D =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK12D();
        this.k13D =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK13D();
        this.k14D =((KtxSeatNormal) ktxRoom.getKtxSeat()).getK14D();
    }
}
