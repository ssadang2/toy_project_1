package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;
import toy.ktx.repository.SaemaulRoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaemaulRoomService {

    private final SaemaulRoomRepository saemaulRoomRepository;

    public List<SaemaulRoom> getSaemaulRoomsToSeatByIdWithFetch(Long id) {
        return saemaulRoomRepository.getSaemaulRoomsToSeatByIdWithFetch(id);
    }

    public List<SaemaulRoom> findAllBySaemaul(Saemaul saemaul) {
        return saemaulRoomRepository.findAllBySaemaul(saemaul);
    }

    @Transactional
    public void save(SaemaulRoom saemaulRoom) {
        saemaulRoomRepository.save(saemaulRoom);
    }
}
