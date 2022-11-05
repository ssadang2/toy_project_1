package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;
import toy.ktx.repository.KtxSeatRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KtxSeatService {

    private final KtxSeatRepository ktxSeatRepository;

    @Transactional
    public void saveKtxSeat(KtxSeat ktxSeat) {
        ktxSeatRepository.save(ktxSeat);
    }

    public Optional<KtxSeat> findKtxSeat(Long ktxSeatId) {
        return ktxSeatRepository.findById(ktxSeatId);
    }

    public Optional<KtxSeat> findByKtxRoom(KtxRoom ktxRoom) {
        return ktxSeatRepository.findByKtxRoom(ktxRoom);
    }
}
