package toy.ktx.domain.dto.api;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class KtxWithRoomSeatDto {

    private Long ktxId;

    private String trainName;

    private List<KtxRoomWithSeatDto> ktxRoomWithSeatDtos;

    public KtxWithRoomSeatDto(Ktx ktx) {
        this.ktxId = ktx.getId();
        this.trainName = ktx.getTrainName();
        this.ktxRoomWithSeatDtos = new ArrayList<>();

        for (KtxRoom ktxRoom : ktx.getKtxRooms()) {
            if (ktxRoom.getGrade().equals(Grade.NORMAL)) {
                ktxRoomWithSeatDtos.add(new KtxRoomWithNormalSeatDto(ktxRoom.getId(), ktxRoom.getRoomName(), ktxRoom));
            } else {
                ktxRoomWithSeatDtos.add(new KtxRoomWithVipSeatDto(ktxRoom.getId(), ktxRoom.getRoomName(), ktxRoom));
            }
        }
    }

    public KtxWithRoomSeatDto(Ktx ktx, List<KtxRoom> ktxRooms) {
        this.ktxId = ktx.getId();
        this.trainName = ktx.getTrainName();
        this.ktxRoomWithSeatDtos = new ArrayList<>();

        for (KtxRoom ktxRoom : ktxRooms) {
            if (ktxRoom.getGrade().equals(Grade.NORMAL)) {
                ktxRoomWithSeatDtos.add(new KtxRoomWithNormalSeatDto(ktxRoom.getId(), ktxRoom.getRoomName(), ktxRoom));
            } else {
                ktxRoomWithSeatDtos.add(new KtxRoomWithVipSeatDto(ktxRoom.getId(), ktxRoom.getRoomName(), ktxRoom));
            }
        }
    }
}
