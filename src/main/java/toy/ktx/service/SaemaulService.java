package toy.ktx.service;

import lombok.RequiredArgsConstructor;
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
}
