package toy.ktx.domain.dto.api;

import lombok.Data;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;

import java.util.ArrayList;
import java.util.List;

@Data
public class KtxWithRoomSeatDto {

    private Long ktxId;

    private String trainName;

    private List<KtxRoomWithSeatDto> ktxRoomList;

    public KtxWithRoomSeatDto(Ktx ktx) {
        this.ktxId = ktx.getId();
        this.trainName = ktx.getTrainName();
        this.ktxRoomList = new ArrayList<>();

        for (KtxRoom ktxRoom : ktx.getKtxRooms()) {
            if (ktxRoom.getGrade().equals(Grade.NORMAL)) {
                ktxRoomList.add(new KtxRoomWithNormalSeatDto(ktxRoom.getId(), ktxRoom.getRoomName(), ktxRoom));
            } else {
                ktxRoomList.add(new KtxRoomWithVipSeatDto(ktxRoom.getId(), ktxRoom.getRoomName(), ktxRoom));
            }
        }
    }
}
