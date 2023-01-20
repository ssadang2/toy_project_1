package toy.ktx.domain.dto.api;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
public class MugunghwaWithRoomSeatDto {

    private Long mugunghwaId;

    private String trainName;

    private List<MugunghwaRoomWithSeatDto> mugunghwaRoomWithSeatDtos;

    public MugunghwaWithRoomSeatDto(Mugunghwa mugunghwa) {
        this.mugunghwaId = mugunghwa.getId();
        this.trainName = mugunghwa.getTrainName();
        this.mugunghwaRoomWithSeatDtos = mugunghwa.getMugunghwaRooms().stream().map(r -> new MugunghwaRoomWithSeatDto(r)).collect(Collectors.toList());
    }

    public MugunghwaWithRoomSeatDto(Mugunghwa mugunghwa, List<MugunghwaRoom> mugunghwaRooms) {
        this.mugunghwaId = mugunghwa.getId();
        this.trainName = mugunghwa.getTrainName();
        this.mugunghwaRoomWithSeatDtos = mugunghwaRooms.stream().map(r -> new MugunghwaRoomWithSeatDto(r)).collect(Collectors.toList());
    }
}
