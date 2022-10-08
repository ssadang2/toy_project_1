package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.repository.KtxRoomRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KtxRoomService {

    private final KtxRoomRepository ktxRoomRepository;

    @Transactional
    public void saveKtxRoom(KtxRoom ktxRoom) {
        ktxRoomRepository.save(ktxRoom);
    }

    public Optional<KtxRoom> findKtxRoom(Long ktxRoomId) {
        return ktxRoomRepository.findById(ktxRoomId);
    }
}
