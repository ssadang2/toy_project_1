package toy.ktx.domain.dto.api;

import lombok.Data;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class SaemaulWithRoomSeatDto {

    private Long saemaulId;

    private String trainName;

    private List<SaemaulRoomWithSeatDto> saemaulRoomWithSeatDtos;

    public SaemaulWithRoomSeatDto(Saemaul saemaul) {
        this.saemaulId = saemaul.getId();
        this.trainName = saemaul.getTrainName();
        this.saemaulRoomWithSeatDtos = saemaul.getSaemaulRooms().stream().map(r -> new SaemaulRoomWithSeatDto(r)).collect(Collectors.toList());
    }

    public SaemaulWithRoomSeatDto(Saemaul saemaul, List<SaemaulRoom> saemaulRooms) {
        this.saemaulId = saemaul.getId();
        this.trainName = saemaul.getTrainName();
        this.saemaulRoomWithSeatDtos = saemaulRooms.stream().map(r -> new SaemaulRoomWithSeatDto(r)).collect(Collectors.toList());
    }
}
