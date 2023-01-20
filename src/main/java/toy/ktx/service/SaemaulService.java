package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.repository.SaemaulRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaemaulService {

    private final SaemaulRepository saemaulRepository;

    public List<Saemaul> getSaemaulToSeatWithFetchAndIn(List<Long> ids) {
        return saemaulRepository.getSaemaulToSeatWithFetchAndIn(ids);
    }

    @Transactional
    public void save(Saemaul saemaul) {
        saemaulRepository.save(saemaul);
    }

    public List<Saemaul> getAllSaemaulToSeatFetch() {
        return saemaulRepository.getAllSaemaulToSeatFetch();
    }

    public Page<Saemaul> findAll(Pageable pageable) {
        return saemaulRepository.findAll(pageable);
    }
}
