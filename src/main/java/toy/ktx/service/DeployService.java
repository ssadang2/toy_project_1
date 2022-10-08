package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Deploy;
import toy.ktx.repository.DeployRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeployService {

    private final DeployRepository deployRepository;

    @Transactional
    public void saveDeploy(Deploy deploy) {
        deployRepository.save(deploy);
    }

    public Optional<Deploy> findDeploy(Long deployId) {
        return deployRepository.findById(deployId);
    }

    public List<Deploy> searchDeploy(String departurePlace, String arrivalPlace,
                                     LocalDateTime departureTime) {

        return deployRepository.searchDeploy(departurePlace, arrivalPlace, departureTime);
    }
}
