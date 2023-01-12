package toy.ktx.domain.dto.api;

import lombok.Data;
import toy.ktx.domain.saemaul.SaemaulRoom;

@Data
public class SaemaulRoomWithSeatDto {
    private Long roomId;

    private String roomName;

    private Boolean s1A;
    private Boolean s2A;
    private Boolean s3A;
    private Boolean s4A;
    private Boolean s5A;
    private Boolean s6A;
    private Boolean s7A;
    private Boolean s8A;
    private Boolean s9A;
    private Boolean s10A;
    private Boolean s11A;
    private Boolean s12A;
    private Boolean s13A;
    private Boolean s14A;

    private Boolean s1B;
    private Boolean s2B;
    private Boolean s3B;
    private Boolean s4B;
    private Boolean s5B;
    private Boolean s6B;
    private Boolean s7B;
    private Boolean s8B;
    private Boolean s9B;
    private Boolean s10B;
    private Boolean s11B;
    private Boolean s12B;
    private Boolean s13B;
    private Boolean s14B;

    private Boolean s1C;
    private Boolean s2C;
    private Boolean s3C;
    private Boolean s4C;
    private Boolean s5C;
    private Boolean s6C;
    private Boolean s7C;
    private Boolean s8C;
    private Boolean s9C;
    private Boolean s10C;
    private Boolean s11C;
    private Boolean s12C;
    private Boolean s13C;
    private Boolean s14C;

    private Boolean s1D;
    private Boolean s2D;
    private Boolean s3D;
    private Boolean s4D;
    private Boolean s5D;
    private Boolean s6D;
    private Boolean s7D;
    private Boolean s8D;
    private Boolean s9D;
    private Boolean s10D;
    private Boolean s11D;
    private Boolean s12D;
    private Boolean s13D;
    private Boolean s14D;

    public SaemaulRoomWithSeatDto(SaemaulRoom saemaulRoom) {
        this.roomId = saemaulRoom.getId();
        this.roomName = saemaulRoom.getRoomName();

        this.s1A = saemaulRoom.getSaemaulSeat().getS1A();
        this.s2A = saemaulRoom.getSaemaulSeat().getS2A();
        this.s3A = saemaulRoom.getSaemaulSeat().getS3A();
        this.s4A = saemaulRoom.getSaemaulSeat().getS4A();
        this.s5A = saemaulRoom.getSaemaulSeat().getS5A();
        this.s6A = saemaulRoom.getSaemaulSeat().getS6A();
        this.s7A = saemaulRoom.getSaemaulSeat().getS7A();
        this.s8A = saemaulRoom.getSaemaulSeat().getS8A();
        this.s9A = saemaulRoom.getSaemaulSeat().getS9A();
        this.s10A = saemaulRoom.getSaemaulSeat().getS10A();
        this.s11A = saemaulRoom.getSaemaulSeat().getS11A();
        this.s12A = saemaulRoom.getSaemaulSeat().getS12A();
        this.s13A = saemaulRoom.getSaemaulSeat().getS13A();
        this.s14A = saemaulRoom.getSaemaulSeat().getS14A();
        this.s1B = saemaulRoom.getSaemaulSeat().getS1B();
        this.s2B = saemaulRoom.getSaemaulSeat().getS2B();
        this.s3B = saemaulRoom.getSaemaulSeat().getS3B();
        this.s4B = saemaulRoom.getSaemaulSeat().getS4B();
        this.s5B = saemaulRoom.getSaemaulSeat().getS5B();
        this.s6B = saemaulRoom.getSaemaulSeat().getS6B();
        this.s7B = saemaulRoom.getSaemaulSeat().getS7B();
        this.s8B = saemaulRoom.getSaemaulSeat().getS8B();
        this.s9B = saemaulRoom.getSaemaulSeat().getS9B();
        this.s10B = saemaulRoom.getSaemaulSeat().getS10B();
        this.s11B = saemaulRoom.getSaemaulSeat().getS11B();
        this.s12B = saemaulRoom.getSaemaulSeat().getS12B();
        this.s13B = saemaulRoom.getSaemaulSeat().getS13B();
        this.s14B = saemaulRoom.getSaemaulSeat().getS14B();
        this.s1C = saemaulRoom.getSaemaulSeat().getS1C();
        this.s2C = saemaulRoom.getSaemaulSeat().getS2C();
        this.s3C = saemaulRoom.getSaemaulSeat().getS3C();
        this.s4C = saemaulRoom.getSaemaulSeat().getS4C();
        this.s5C = saemaulRoom.getSaemaulSeat().getS5C();
        this.s6C = saemaulRoom.getSaemaulSeat().getS6C();
        this.s7C = saemaulRoom.getSaemaulSeat().getS7C();
        this.s8C = saemaulRoom.getSaemaulSeat().getS8C();
        this.s9C = saemaulRoom.getSaemaulSeat().getS9C();
        this.s10C = saemaulRoom.getSaemaulSeat().getS10C();
        this.s11C = saemaulRoom.getSaemaulSeat().getS11C();
        this.s12C = saemaulRoom.getSaemaulSeat().getS12C();
        this.s13C = saemaulRoom.getSaemaulSeat().getS13C();
        this.s14C = saemaulRoom.getSaemaulSeat().getS14C();
        this.s1D = saemaulRoom.getSaemaulSeat().getS1D();
        this.s2D = saemaulRoom.getSaemaulSeat().getS2D();
        this.s3D = saemaulRoom.getSaemaulSeat().getS3D();
        this.s4D = saemaulRoom.getSaemaulSeat().getS4D();
        this.s5D = saemaulRoom.getSaemaulSeat().getS5D();
        this.s6D = saemaulRoom.getSaemaulSeat().getS6D();
        this.s7D = saemaulRoom.getSaemaulSeat().getS7D();
        this.s8D = saemaulRoom.getSaemaulSeat().getS8D();
        this.s9D = saemaulRoom.getSaemaulSeat().getS9D();
        this.s10D =saemaulRoom.getSaemaulSeat().getS10D();
        this.s11D =saemaulRoom.getSaemaulSeat().getS11D();
        this.s12D =saemaulRoom.getSaemaulSeat().getS12D();
        this.s13D =saemaulRoom.getSaemaulSeat().getS13D();
        this.s14D =saemaulRoom.getSaemaulSeat().getS14D();
    }
}
