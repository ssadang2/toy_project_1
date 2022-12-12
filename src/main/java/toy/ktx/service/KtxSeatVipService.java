package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.dto.projections.VipSeatDto;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeatNormal;
import toy.ktx.domain.ktx.KtxSeatVip;
import toy.ktx.repository.KtxSeatVipRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KtxSeatVipService {

    private final KtxSeatVipRepository ktxSeatVipRepository;

    public Optional<KtxSeatVip> findById(Long id) {
        return ktxSeatVipRepository.findById(id);
    }

    public VipSeatDto findVipDtoById(Long id) {
        return ktxSeatVipRepository.findVipDtoById(id);
    }

    @Transactional
    public void save(KtxSeatVip ktxSeatVip) {
        ktxSeatVipRepository.save(ktxSeatVip);
    }

//    public List<KtxSeatVip> findKtxSeatVipWithDeployIdFetch(Long id) {
//        return ktxSeatVipRepository.findKtxSeatVipWithDeployIdFetch(id);
//    }
}
