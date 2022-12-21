package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.repository.KtxRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KtxService {

    private final KtxRepository ktxRepository;

    @Transactional
    public void saveKtx(Ktx ktx) {
        ktxRepository.save(ktx);
    }

    public Optional<Ktx> findKtx(Long ktxId) {
        return ktxRepository.findById(ktxId);
    }

    public List<Ktx> getKtxToSeatWithFetchAndIn(List<Long> ids) {
        return ktxRepository.getKtxToSeatWithFetchAndIn(ids);
    }
}
