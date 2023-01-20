package toy.ktx.domain.dto.api;

import lombok.Data;

@Data
public class KtxRoomWithSeatDto {

    private Long roomId;

    private String roomName;

    public KtxRoomWithSeatDto(Long roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }
}
