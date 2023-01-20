package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.comparator.DeployComparator;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.constant.StationsConst;
import toy.ktx.domain.constant.TrainNameConst;
import toy.ktx.domain.dto.CreateDeployForm;
import toy.ktx.domain.dto.DeploySearchDto;
import toy.ktx.domain.enums.Authorizations;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.*;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.domain.mugunhwa.MugunghwaSeat;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;
import toy.ktx.domain.saemaul.SaemaulSeat;
import toy.ktx.service.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final ReservationService reservationService;
    private final KtxRoomService ktxRoomService;
    private final MugunghwaRoomService mugunghwaRoomService;
    private final SaemaulRoomService saemaulRoomService;
    private final KtxSeatService ktxSeatService;
    private final MugunghwaSeatService mugunghwaSeatService;
    private final SaemaulSeatService saemaulSeatService;
    private final KtxService ktxService;
    private final MugunghwaService mugunghwaService;
    private final SaemaulService saemaulService;
    private final KtxSeatNormalService ktxSeatNormalService;
    private final KtxSeatVipService ktxSeatVipService;
    private final DeployService deployService;

    //컨트롤 URI
    //시간표 저장을 처리하는 컨트롤러
    @PostMapping("/my-page/save-deploy")
    public String saveDeploy(@Valid @ModelAttribute CreateDeployForm createDeployForm, BindingResult bindingResult,
                             @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member, Model model) {

        //여러가지 validation
        if(createDeployForm.getTimeOfGoing().length() != 5) {
            bindingResult.reject("noCorrectTimeFormatGoing", null);
        }

        if(createDeployForm.getTimeOfComing().length() != 5) {
            bindingResult.reject("noCorrectTimeFormatComing", null);
        }

        if(createDeployForm.getTimeOfGoing().length() == 5 && !createDeployForm.getTimeOfGoing().substring(2,3).equals(":")) {
            bindingResult.reject("noColonGoing", null);
        }

        if(createDeployForm.getTimeOfComing().length() == 5 && !createDeployForm.getTimeOfComing().substring(2,3).equals(":")) {
            bindingResult.reject("noColonComing", null);
        }

        try {
            if(createDeployForm.getTimeOfGoing().length() == 5 &&
                    ((Integer.parseInt(createDeployForm.getTimeOfGoing().substring(0 ,2)) > 24 ||
                            Integer.parseInt(createDeployForm.getTimeOfGoing().substring(0 ,2)) < 0) ||
                            (Integer.parseInt(createDeployForm.getTimeOfGoing().substring(3)) >60 ||
                                    Integer.parseInt(createDeployForm.getTimeOfGoing().substring(3)) < 0))) {
                bindingResult.reject("noCorrectTimeFormatGoing", null);
            }
        } catch (Exception e) {
            bindingResult.reject("noCorrectTimeFormatGoing", null);
        }

        try {
            if(createDeployForm.getTimeOfComing().length() == 5 &&
                    ((Integer.parseInt(createDeployForm.getTimeOfComing().substring(0 ,2)) > 24 ||
                            Integer.parseInt(createDeployForm.getTimeOfComing().substring(0 ,2)) < 0) ||
                            (Integer.parseInt(createDeployForm.getTimeOfComing().substring(3)) >60 ||
                                    Integer.parseInt(createDeployForm.getTimeOfComing().substring(3)) < 0))) {
                bindingResult.reject("noCorrectTimeFormatComing", null);
            }
        } catch (Exception e) {
            bindingResult.reject("noCorrectTimeFormatComing", null);
        }

        if(!Arrays.asList(StationsConst.stations).contains(createDeployForm.getDeparturePlace())
                || !Arrays.asList(StationsConst.stations).contains(createDeployForm.getArrivalPlace())) {
            bindingResult.reject("noStation", null);
        }

        if(!Arrays.asList(TrainNameConst.trains).contains(createDeployForm.getTrainName())) {
            bindingResult.reject("noTrain", null);
        }

        if(bindingResult.hasErrors()) {
            //logic상 member가 존재하지 않을 수 없다고 판단하여 따로 null check logic 넣지 않았음
            List<Deploy> deployList = deployService.getDeploysToTrain();
            Collections.sort(deployList, new DeployComparator());
            List<String> durations = getDuration(deployList);

            model.addAttribute("member", member);
            model.addAttribute("deployList", deployList);
            model.addAttribute("durations", durations);
            //updated
            model.addAttribute("deploySearchDto", new DeploySearchDto());
            return "mypage/adminMYPage";
        }
        //실제 db에 입력된 정보에 기반해 deploy를 입력하는 logic(success logic)
        Deploy deploy = new Deploy();

        String dateTimeOfGoingString = createDeployForm.getDateOfGoing() + "T" + createDeployForm.getTimeOfGoing();
        LocalDateTime dateTimeOfGoing = getLocalDateTime(dateTimeOfGoingString);

        String dateTimeOfComingString = createDeployForm.getDateOfComing() + "T" + createDeployForm.getTimeOfComing();
        LocalDateTime dateTimeOfComing = getLocalDateTime(dateTimeOfComingString);

        deploy.setDeparturePlace(createDeployForm.getDeparturePlace());
        deploy.setArrivalPlace(createDeployForm.getArrivalPlace());
        deploy.setDepartureTime(dateTimeOfGoing);
        deploy.setArrivalTime(dateTimeOfComing);

        //기차가 ktx라면
        if (createDeployForm.getTrainName().contains("KTX")) {
            Ktx ktx = new Ktx();
            ktx.setTrainName(createDeployForm.getTrainName());
            ktxService.saveKtx(ktx);

            KtxSeatNormal ktxSeat1 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatNormal ktxSeat2 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatVip ktxSeat3 = new KtxSeatVip(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false);

            KtxSeatVip ktxSeat4 = new KtxSeatVip(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false);

            KtxSeatVip ktxSeat5 = new KtxSeatVip( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false);

            KtxSeatNormal ktxSeat6 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatNormal ktxSeat7 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatNormal ktxSeat8 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatNormal ktxSeat9 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatNormal ktxSeat10 = new KtxSeatNormal( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            ktxSeatNormalService.save(ktxSeat1);
            ktxSeatNormalService.save(ktxSeat2);
            ktxSeatVipService.save(ktxSeat3);
            ktxSeatVipService.save(ktxSeat4);
            ktxSeatVipService.save(ktxSeat5);
            ktxSeatNormalService.save(ktxSeat6);
            ktxSeatNormalService.save(ktxSeat7);
            ktxSeatNormalService.save(ktxSeat8);
            ktxSeatNormalService.save(ktxSeat9);
            ktxSeatNormalService.save(ktxSeat10);

            KtxRoom room1 = new KtxRoom("room1", ktx, Grade.NORMAL, ktxSeat1);
            KtxRoom room2 = new KtxRoom("room2", ktx, Grade.NORMAL, ktxSeat2);
            KtxRoom room3 = new KtxRoom("room3", ktx, Grade.VIP, ktxSeat3);
            KtxRoom room4 = new KtxRoom("room4", ktx, Grade.VIP, ktxSeat4);
            KtxRoom room5 = new KtxRoom("room5", ktx, Grade.VIP, ktxSeat5);
            KtxRoom room6 = new KtxRoom("room6", ktx, Grade.NORMAL, ktxSeat6);
            KtxRoom room7 = new KtxRoom("room7", ktx, Grade.NORMAL, ktxSeat7);
            KtxRoom room8 = new KtxRoom("room8", ktx, Grade.NORMAL, ktxSeat8);
            KtxRoom room9 = new KtxRoom("room9", ktx, Grade.NORMAL, ktxSeat9);
            KtxRoom room10 = new KtxRoom("room10", ktx, Grade.NORMAL,ktxSeat10);

            ktxRoomService.saveKtxRoom(room1);
            ktxRoomService.saveKtxRoom(room2);
            ktxRoomService.saveKtxRoom(room3);
            ktxRoomService.saveKtxRoom(room4);
            ktxRoomService.saveKtxRoom(room5);
            ktxRoomService.saveKtxRoom(room6);
            ktxRoomService.saveKtxRoom(room7);
            ktxRoomService.saveKtxRoom(room8);
            ktxRoomService.saveKtxRoom(room9);
            ktxRoomService.saveKtxRoom(room10);

            deploy.setTrain(ktx);
            deployService.saveDeploy(deploy);
            return "redirect:/my-page";

        //기차가 무궁화호라면
        } else if (createDeployForm.getTrainName().contains("MUGUNGHWA")) {
            Mugunghwa mugunghwa = new Mugunghwa();
            mugunghwa.setTrainName(createDeployForm.getTrainName());
            mugunghwaService.save(mugunghwa);

            MugunghwaSeat seat1 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat2 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat3 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat4 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat5 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            mugunghwaSeatService.save(seat1);
            mugunghwaSeatService.save(seat2);
            mugunghwaSeatService.save(seat3);
            mugunghwaSeatService.save(seat4);
            mugunghwaSeatService.save(seat5);

            MugunghwaRoom room1 = new MugunghwaRoom("room1", mugunghwa, seat1);
            MugunghwaRoom room2 = new MugunghwaRoom("room2", mugunghwa, seat2);
            MugunghwaRoom room3 = new MugunghwaRoom("room3", mugunghwa, seat3);
            MugunghwaRoom room4 = new MugunghwaRoom("room4", mugunghwa, seat4);
            MugunghwaRoom room5 = new MugunghwaRoom("room5", mugunghwa, seat5);

            mugunghwaRoomService.save(room1);
            mugunghwaRoomService.save(room2);
            mugunghwaRoomService.save(room3);
            mugunghwaRoomService.save(room4);
            mugunghwaRoomService.save(room5);

            deploy.setTrain(mugunghwa);
            deployService.saveDeploy(deploy);
            return "redirect:/my-page";
            //기차 새마을호라면
        } else {
            Saemaul saemaul = new Saemaul();
            saemaul.setTrainName(createDeployForm.getTrainName());
            saemaulService.save(saemaul);

            SaemaulSeat seat1 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat2 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat3 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat4 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat5 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            saemaulSeatService.save(seat1);
            saemaulSeatService.save(seat2);
            saemaulSeatService.save(seat3);
            saemaulSeatService.save(seat4);
            saemaulSeatService.save(seat5);

            SaemaulRoom room1 = new SaemaulRoom("room1", saemaul, seat1);
            SaemaulRoom room2 = new SaemaulRoom("room2", saemaul, seat2);
            SaemaulRoom room3 = new SaemaulRoom("room3", saemaul, seat3);
            SaemaulRoom room4 = new SaemaulRoom("room4", saemaul, seat4);
            SaemaulRoom room5 = new SaemaulRoom("room5", saemaul, seat5);

            saemaulRoomService.save(room1);
            saemaulRoomService.save(room2);
            saemaulRoomService.save(room3);
            saemaulRoomService.save(room4);
            saemaulRoomService.save(room5);

            deploy.setTrain(saemaul);
            deployService.saveDeploy(deploy);
            return "redirect:/my-page";
        }
    }

    //시간표 삭제를 담당하는 컨트롤러
    @PostMapping("/my-page/admin/deploys")
    public String eraseDeployByAdmin(@RequestParam Long deployId) {
        //관련 기차 관련 기차 호실 관련 기차 좌석 모두 deploy(시간표)가 생명주기를 관리하기 때문에 deploy만 지워줘도 됨
        deployService.deleteById(deployId);
        return "redirect:/my-page";
    }

    //시간표 search query를 처리하는 컨트롤러
    @PostMapping("/my-page/admin/search-deploys")
    public String searchDeploys(@Valid @ModelAttribute DeploySearchDto deploySearchDto,
                                BindingResult bindingResult,
                                @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                                Model model) {

        //여러 validation
        if (!StringUtils.hasText(deploySearchDto.getDateOfGoing()) && StringUtils.hasText(deploySearchDto.getTimeOfGoing())) {
            bindingResult.reject("noDateButTime", null);
        }

        if (!StringUtils.hasText(deploySearchDto.getDateOfComing()) && StringUtils.hasText(deploySearchDto.getTimeOfComing())) {
            bindingResult.reject("noDateButTime", null);
        }

        if(StringUtils.hasText(deploySearchDto.getTimeOfGoing()) && deploySearchDto.getTimeOfGoing().length() != 5) {
            bindingResult.reject("noCorrectTimeFormatGoing", null);
        }

        if(StringUtils.hasText(deploySearchDto.getTimeOfComing()) && deploySearchDto.getTimeOfComing().length() != 5) {
            bindingResult.reject("noCorrectTimeFormatComing", null);
        }

        if(deploySearchDto.getTimeOfGoing().length() == 5 && !deploySearchDto.getTimeOfGoing().substring(2,3).equals(":")) {
            bindingResult.reject("noColonGoing", null);
        }

        if(deploySearchDto.getTimeOfComing().length() == 5 && !deploySearchDto.getTimeOfComing().substring(2,3).equals(":")) {
            bindingResult.reject("noColonComing", null);
        }

        try {
            if(deploySearchDto.getTimeOfGoing().length() == 5 &&
                    ((Integer.parseInt(deploySearchDto.getTimeOfGoing().substring(0 ,2)) > 24 ||
                            Integer.parseInt(deploySearchDto.getTimeOfGoing().substring(0 ,2)) < 0) ||
                            (Integer.parseInt(deploySearchDto.getTimeOfGoing().substring(3)) >60 ||
                                    Integer.parseInt(deploySearchDto.getTimeOfGoing().substring(3)) < 0))) {
                bindingResult.reject("noCorrectTimeFormatGoing", null);
            }
        } catch (Exception e) {
            bindingResult.reject("noCorrectTimeFormatGoing", null);
        }

        try {
            if(deploySearchDto.getTimeOfComing().length() == 5 &&
                    ((Integer.parseInt(deploySearchDto.getTimeOfComing().substring(0 ,2)) > 24 ||
                            Integer.parseInt(deploySearchDto.getTimeOfComing().substring(0 ,2)) < 0) ||
                            (Integer.parseInt(deploySearchDto.getTimeOfComing().substring(3)) >60 ||
                                    Integer.parseInt(deploySearchDto.getTimeOfComing().substring(3)) < 0))) {
                bindingResult.reject("noCorrectTimeFormatComing", null);
            }
        } catch (Exception e) {
            bindingResult.reject("noCorrectTimeFormatComing", null);
        }

        if (bindingResult.hasErrors()) {
            List<Deploy> deployList = deployService.getDeploysToTrain();
            Collections.sort(deployList, new DeployComparator());
            List<String> durations = getDuration(deployList);

            model.addAttribute("member", member);
            model.addAttribute("deployList", deployList);
            model.addAttribute("durations", durations);
            model.addAttribute("createDeployForm", new CreateDeployForm());
            return "mypage/adminMYPage";
        }
        //success logic(search deploys)
        LocalDateTime goingTimeCond = null;
        LocalDateTime comingTimeCond = null;

        if (StringUtils.hasText(deploySearchDto.getDateOfGoing())) {
            if (StringUtils.hasText(deploySearchDto.getTimeOfGoing())) {
                String dateTimeOfGoing = deploySearchDto.getDateOfGoing() + "T" + deploySearchDto.getTimeOfGoing();
                goingTimeCond = getLocalDateTime(dateTimeOfGoing);
            } else {
                String dateTimeOfGoing = deploySearchDto.getDateOfGoing() + "T" + "00:00";
                goingTimeCond = getLocalDateTime(dateTimeOfGoing);
            }
        }

        if (StringUtils.hasText(deploySearchDto.getDateOfComing())) {
            if (StringUtils.hasText(deploySearchDto.getTimeOfComing())) {
                String dateTimeOfComing = deploySearchDto.getDateOfComing() + "T" + deploySearchDto.getTimeOfComing();
                comingTimeCond = getLocalDateTime(dateTimeOfComing);
            } else {
                String dateTimeOfComing = deploySearchDto.getDateOfComing() + "T" + "00:00";
                comingTimeCond = getLocalDateTime(dateTimeOfComing);
            }
        }
        List<Deploy> deployList = deployService.searchDeploys(goingTimeCond, comingTimeCond);
        Collections.sort(deployList, new DeployComparator());
        List<String> durations = getDuration(deployList);

        model.addAttribute("member", member);
        model.addAttribute("deployList", deployList);
        model.addAttribute("durations", durations);
        model.addAttribute("createDeployForm", new CreateDeployForm());
        return "mypage/adminMyPage";
    }

    //관리자가 어떤 시간표의 예약 현황에 접근하는 것을 처리하는 컨트롤러
    @GetMapping("/my-page/admin/reservations/{deployId}")
    public String getReservationsByAdmin(@PathVariable Long deployId,
                                         @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                                         HttpServletResponse response,
                                         Model model) throws IOException {
        //입터셉터에서 걸려서 딱히 잡을 필요없을 듯?
        if (member == null) {
            response.sendError(401, "인증되지 않은 사용자의 접근");
            return null;
        }

        if (!member.getAuthorizations().equals(Authorizations.ADMIN)) {
            response.sendError(403, "인가 받지 않은 사용자의 접근");
            return null;
        }

        Deploy deploy = deployService.getDeployToReservationById(deployId);
        List<Reservation> reservations = deploy.getReservations();
        List<Deploy> deploys = new ArrayList<>();

        if (!reservations.isEmpty()) {
            for (Reservation reservation : reservations) {
                deploys.add(deploy);
            }
            List<String> durations = getDuration(deploys);
            model.addAttribute("reservations", reservations);
            model.addAttribute("durations", durations);
        }
        model.addAttribute("member", member);
        model.addAttribute("deploy", deploy);

        return "mypage/adminReservationsPage";
    }

    //관리자가 어떤 시간표의 예약을 지우는 것을 처리하는 컨트롤러
    @PostMapping("/my-page/admin/reservations/{deployId}")
    public String cancelReservationByAdmin(@RequestParam Long reservationId,
                                           @PathVariable Long deployId,
                                           @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member) {
        //예약 삭제 로직
        //예상 select 쿼리 2개 -> 실제 3개 select passenger 나가는 이유 => 프록시 초기화해야 pk 값을 가져올 수 있기 때문에
        if (reservationId != null) {
            Optional<Reservation> foundReservation = reservationService.getReservationToTrainByIdWithFetch(reservationId);
            if (foundReservation.isPresent()) {
                Reservation reservation = foundReservation.get();
                if (reservation.getDeploy().getTrain().getTrainName().contains("KTX")) {
                    Ktx train = (Ktx) reservation.getDeploy().getTrain();
                    List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByIdWithFetch(train.getId());
                    Optional<KtxRoom> roomOptional = ktxRooms.stream().filter(r -> r.getRoomName().equals(reservation.getRoomName())).findFirst();
                    KtxRoom ktxRoom = roomOptional.get();
                    KtxSeat ktxSeat = ktxRoom.getKtxSeat();

                    //reservation 등의 entity 뿐만 아니라 seat entity 안의 자리까지 체크 해제해줘야 됨
                    if (ktxRoom.getGrade().equals(Grade.NORMAL)) {
                        ktxSeat = (KtxSeatNormal) ktxSeat;
                    } else {
                        ktxSeat = (KtxSeatVip) ktxSeat;
                    }

                    ktxSeatService.updateSeatsWithReflection(ktxSeat, reservation.getSeats());
                } else if (reservation.getDeploy().getTrain().getTrainName().contains("MUGUNGHWA")) {
                    Mugunghwa train = (Mugunghwa) reservation.getDeploy().getTrain();
                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(train.getId());
                    Optional<MugunghwaRoom> roomOptional = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(reservation.getRoomName())).findFirst();
                    MugunghwaRoom mugunghwaRoom = roomOptional.get();
                    MugunghwaSeat mugunghwaSeat = mugunghwaRoom.getMugunghwaSeat();

                    mugunghwaSeatService.updateSeatsWithReflection(mugunghwaSeat, reservation.getSeats());
                } else {
                    Saemaul train = (Saemaul) reservation.getDeploy().getTrain();
                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(train.getId());
                    Optional<SaemaulRoom> roomOptional = saemaulRooms.stream().filter(r -> r.getRoomName().equals(reservation.getRoomName())).findFirst();
                    SaemaulRoom saemaulRoom = roomOptional.get();
                    SaemaulSeat saemaulSeat = saemaulRoom.getSaemaulSeat();

                    saemaulSeatService.updateSeatsWithReflection(saemaulSeat, reservation.getSeats());
                }
            }

            //cascade option을 켰기 때문에 passenger를 굳이 수동으로 안 지워줘도 됨
            reservationService.deleteById(reservationId);
        }
        //prg
        return "redirect:/my-page/admin/reservations/" + deployId;
    }

    //string -> LocalDateTime으로 바꿔주는 메소드
    private LocalDateTime getLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTime, formatter);
    }

    //시간표마다 걸리는 기간을 계산하는 메소드
    private List<String> getDuration(List<Deploy> deploys) {
        List<String> durations = new ArrayList<>();
        for (Deploy deploy : deploys) {
            LocalDateTime departureTime = deploy.getDepartureTime();
            LocalDateTime arrivalTime = deploy.getArrivalTime();

            Duration duration = Duration.between(departureTime, arrivalTime);

            long toMinute = duration.getSeconds() / Long.valueOf(60);
            long hour = toMinute / Long.valueOf(60);
            long minute = toMinute % Long.valueOf(60);

            durations.add(hour + "시간 " + minute + "분");
        }
        return durations;
    }
}
