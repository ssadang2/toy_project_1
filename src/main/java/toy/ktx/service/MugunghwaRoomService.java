package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.repository.MugunghwaRoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MugunghwaRoomService {

    private final MugunghwaRoomRepository mugunghwaRoomRepository;

    public List<MugunghwaRoom> getMugunghwaRoomsToSeatByIdWithFetch(Long id) {
        return mugunghwaRoomRepository.getMugunghwaRoomsToSeatByIdWithFetch(id);
    }
}
