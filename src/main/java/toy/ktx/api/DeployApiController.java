package toy.ktx.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import toy.ktx.service.DeployService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DeployApiController {

    private final DeployService deployService;
}
