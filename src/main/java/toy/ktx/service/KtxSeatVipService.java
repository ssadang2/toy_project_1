package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.dto.projections.KtxVipSeatDto;
import toy.ktx.domain.ktx.KtxSeatVip;
import toy.ktx.repository.KtxSeatVipRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KtxSeatVipService {

    private final KtxSeatVipRepository ktxSeatVipRepository;

    public Optional<KtxSeatVip> findById(Long id) {
        return ktxSeatVipRepository.findById(id);
    }

    public KtxVipSeatDto findVipDtoById(Long id) {
        return ktxSeatVipRepository.findVipDtoById(id);
    }

    @Transactional
    public void save(KtxSeatVip ktxSeatVip) {
        ktxSeatVipRepository.save(ktxSeatVip);
    }
}
