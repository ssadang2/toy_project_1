package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeatNormal;
import toy.ktx.repository.KtxSeatNormalRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KtxSeatNormalService {

    private final KtxSeatNormalRepository ktxSeatNormalRepository;

    public Optional<KtxSeatNormal> findById(Long id) {
        return ktxSeatNormalRepository.findById(id);
    }

    public NormalSeatDto findNormalDtoById (Long id) {
        return ktxSeatNormalRepository.findNormalDtoById(id);
    }

    @Transactional
    public void save(KtxSeatNormal ktxSeatNormal) {
        ktxSeatNormalRepository.save(ktxSeatNormal);
    }

//    public List<KtxSeatNormal> findKtxSeatNormalWithDeployIdFetch(Long id) {
//        return ktxSeatNormalRepository.findKtxSeatNormalWithDeployIdFetch(id);
//    }
}
