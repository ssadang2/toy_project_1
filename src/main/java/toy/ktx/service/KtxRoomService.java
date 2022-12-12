package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.repository.KtxRoomRepository;

import java.util.List;
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

    public List<KtxRoom> findByKtx(Ktx ktx) {
        return ktxRoomRepository.findByKtx(ktx);
    }

    public List<KtxRoom> findByKtxAndGrade(Ktx ktx, Grade grade) {
        return ktxRoomRepository.findByKtxAndGrade(ktx, grade);
    }

    public List<KtxRoom> findKtxRoomWithTrainWithDeploy(Long id) {
        //deploy id임
        return ktxRoomRepository.findKtxRoomWithTrainWithDeploy(id);
    }

    public List<KtxRoom> getKtxRoomWithSeatFetch(Long id) {
        return ktxRoomRepository.getKtxRoomsWithSeatFetch(id);
    }
}
