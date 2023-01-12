package toy.ktx.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.ktx.domain.dto.api.MugunghwaWithRoomSeatDto;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.service.DeployService;
import toy.ktx.service.MugunghwaRoomService;
import toy.ktx.service.MugunghwaService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MugunghwaApiController {

    private final DeployService deployService;
    private final MugunghwaService mugunghwaService;
    private final MugunghwaRoomService mugunghwaRoomService;

    //fetch join + no paing v3
    @GetMapping("/api/mugunghwas/fetch")
    public List<MugunghwaWithRoomSeatDto> getAllKtxToSeatFetch() {
        //미리 당기기
        deployService.findAll();
        return mugunghwaService.getAllMugunghwaToSeatFetch().stream().map(m -> new MugunghwaWithRoomSeatDto(m)).collect(Collectors.toList());
    }

    //batch + paging v3.1
    @GetMapping("/api/mugunghwas/paging")
    public Page<MugunghwaWithRoomSeatDto> getAllKtxToSeat(Pageable pageable) {
        deployService.findAll();
        List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatFetch();

        return mugunghwaService.findAll(pageable).map(m -> new MugunghwaWithRoomSeatDto(m, mugunghwaRooms));

    }
}
