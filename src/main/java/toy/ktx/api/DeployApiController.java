package toy.ktx.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import toy.ktx.domain.dto.api.DeployWithTrainDto;
import toy.ktx.repository.query.DeploySearchRepository;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DeployApiController {

    private final DeploySearchRepository deploySearchRepository;

    //paging + dto 조회 + queryDsl
    @GetMapping("/api/deploys")
    public Page<DeployWithTrainDto> searchDeploys(@RequestParam(required = false)LocalDateTime goingTimeCond,
                                                  @RequestParam(required = false)LocalDateTime comingTimeCond,
                                                  Pageable pageable) {

        return deploySearchRepository.searchDeployDtos(goingTimeCond, comingTimeCond, pageable);
    }
}
