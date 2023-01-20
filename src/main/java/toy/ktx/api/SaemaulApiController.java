package toy.ktx.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.ktx.domain.dto.api.MugunghwaWithRoomSeatDto;
import toy.ktx.domain.dto.api.SaemaulWithRoomSeatDto;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.domain.saemaul.SaemaulRoom;
import toy.ktx.service.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SaemaulApiController {

    private final DeployService deployService;
    private final SaemaulService saemaulService;
    private final SaemaulRoomService saemaulRoomService;

    //fetch join + no paing v3
    @GetMapping("/api/saemauls/fetch")
    public List<SaemaulWithRoomSeatDto> getAllKtxToSeatFetch() {
        deployService.findAll();
        return saemaulService.getAllSaemaulToSeatFetch().stream().map(s -> new SaemaulWithRoomSeatDto(s)).collect(Collectors.toList());
    }

    //batch + paging v3.1
    @GetMapping("/api/saemauls/paging")
    public Page<SaemaulWithRoomSeatDto> getAllKtxToSeat(Pageable pageable) {
        deployService.findAll();
        List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatFetch();

        return saemaulService.findAll(pageable).map(s -> new SaemaulWithRoomSeatDto(s, saemaulRooms));

    }
}
