package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.ScheduleForm;
import toy.ktx.service.DeployService;
import toy.ktx.service.KtxRoomService;
import toy.ktx.service.KtxSeatService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SeatController {

    private final KtxRoomService ktxRoomService;
    private final KtxSeatService ktxSeatService;
    private final DeployService deployService;

    @PostMapping("/seat")
    public String chooseSeat(@ModelAttribute DeployForm deployForm,
                       Model model) {

        log.info("시발{}", deployForm);

        if (deployForm.getDeployIdOfComing() == null) {
            model.addAttribute("deployOfGoing", deployService.findDeploy(deployForm.getDeployIdOfGoing()).get());
            return "chooseSeat";
        }

        model.addAttribute("deployOfGoing", deployService.findDeploy(deployForm.getDeployIdOfGoing()).get());
        model.addAttribute("deployOfComing", deployService.findDeploy(deployForm.getDeployIdOfComing()).get());
        return "chooseSeat";
    }
}
