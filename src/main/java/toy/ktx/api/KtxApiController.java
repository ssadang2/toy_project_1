package toy.ktx.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.ktx.domain.dto.api.KtxWithRoomSeatDto;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.service.DeployService;
import toy.ktx.service.KtxRoomService;
import toy.ktx.service.KtxService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KtxApiController {

    private final KtxService ktxService;
    private final DeployService deployService;
    private final KtxRoomService ktxRoomService;

    //fetch join + no paing v3
    //where 조건으로 특정 deploy만 가져오게 하는 것도 실용성 있을 듯
    @GetMapping("/api/ktxs/fetch")
    public List<KtxWithRoomSeatDto> getAllKtxToSeatFetch() {
        //미리 당기기 -> 이거 안 하면 oneToOne 때문에 쿼리 엄청 나감
        deployService.findAll();
        return ktxService.getAllKtxToSeatFetch().stream().map(k -> new KtxWithRoomSeatDto(k)).collect(Collectors.toList());
    }

    //batch + paging v3.1
    @GetMapping("/api/ktxs/paging")
    public Page<KtxWithRoomSeatDto> getAllKtxToSeat(Pageable pageable) {
        //미리 당기기
        deployService.findAll();
        //미리 당기기 -> 안 하면 프록시 때문에 casting이 안 됨
        List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatFetch();

        //미리 select한 KtxRooms를 생성자에 넘겨주지 않으면 ktx.getKtxRooms()를 할 때 또 초기화해야 해서 쿼리 하나 더 나감
        return ktxService.findAll(pageable).map(k -> new KtxWithRoomSeatDto(k, ktxRooms));
    }
}
