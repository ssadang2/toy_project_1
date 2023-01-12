package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaSeat;
import toy.ktx.repository.MugunghwaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MugunghwaService {

    private final MugunghwaRepository mugunghwaRepository;

    public List<Mugunghwa> getMugunghwaToSeatWithFetchAndIn(List<Long> ids) {
        return mugunghwaRepository.getMugunghwaToSeatWithFetchAndIn(ids);
    }

    @Transactional
    public void save(Mugunghwa mugunghwa) {
        mugunghwaRepository.save(mugunghwa);
    }

    public List<Mugunghwa> getAllMugunghwaToSeatFetch() {
        return mugunghwaRepository.getAllMugunghwaToSeatFetch();
    }

    public Page<Mugunghwa> findAll(Pageable pageable) {
        return mugunghwaRepository.findAll(pageable);
    }
}
