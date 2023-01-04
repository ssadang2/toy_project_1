package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Deploy;
import toy.ktx.repository.DeployRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        int year = departureTime.getYear();
        int monthValue = departureTime.getMonthValue();
        int dayOfMonth = departureTime.getDayOfMonth();

        LocalDateTime until = LocalDateTime.of(year, monthValue, dayOfMonth, 23, 59, 59);

        // 출발 시간이 1시간 단위더라도 이미 지나간 시간대는 걸러줘야 됨
        if (departureTime.format(DateTimeFormatter.ISO_DATE).equals(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))) {
            departureTime = LocalDateTime.now();
        }

        return deployRepository.searchDeploy(departurePlace, arrivalPlace, departureTime, until);
    }

    public List<Deploy> searchDeployToTrain(String departurePlace, String arrivalPlace,
                                     LocalDateTime departureTime) {

        int year = departureTime.getYear();
        int monthValue = departureTime.getMonthValue();
        int dayOfMonth = departureTime.getDayOfMonth();

        LocalDateTime until = LocalDateTime.of(year, monthValue, dayOfMonth, 23, 59, 59);

        // 출발 시간이 1시간 단위더라도 이미 지나간 시간대는 걸러줘야 됨
        if (departureTime.format(DateTimeFormatter.ISO_DATE).equals(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))) {
            departureTime = LocalDateTime.now();
        }

        return deployRepository.searchDeployToTrain(departurePlace, arrivalPlace, departureTime, until);
    }

    public Deploy getDeployToTrainById(Long id) {
        return deployRepository.getDeployToTrainById(id);
    }

    public List<Deploy> findAll() {
        return deployRepository.findAll();
    }

    public List<Deploy> getDeploysToTrain() {
        return deployRepository.getDeploysToTrain();
    }

    public Deploy getDeployToReservationById(Long id) {
        return deployRepository.getDeployToReservationById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        deployRepository.deleteById(id);
    }
}
