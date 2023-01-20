package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.dto.projections.KtxNormalSeatDto;
import toy.ktx.domain.ktx.KtxSeatNormal;
import toy.ktx.repository.KtxSeatNormalRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KtxSeatNormalService {

    private final KtxSeatNormalRepository ktxSeatNormalRepository;

    public Optional<KtxSeatNormal> findById(Long id) {
        return ktxSeatNormalRepository.findById(id);
    }

    public KtxNormalSeatDto findNormalDtoById (Long id) {
        return ktxSeatNormalRepository.findNormalDtoById(id);
    }

    @Transactional
    public void save(KtxSeatNormal ktxSeatNormal) {
        ktxSeatNormalRepository.save(ktxSeatNormal);
    }
}
