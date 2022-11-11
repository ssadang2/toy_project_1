package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.PassengerDto;
import toy.ktx.domain.dto.projections.SeatDto;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.service.DeployService;
import toy.ktx.service.KtxRoomService;
import toy.ktx.service.KtxSeatService;
import toy.ktx.service.KtxService;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ReservationController {

    private final KtxRoomService ktxRoomService;
    private final KtxSeatService ktxSeatService;
    private final KtxService ktxService;
    private final DeployService deployService;
    private Integer roomNo = 0;

    @PostMapping("/reservation")
    public String reserve(@ModelAttribute SeatDto seatDto,
                          BindingResult bindingResult,
                          @ModelAttribute DeployForm deployForm,
                          @RequestParam Integer beforeOccupied,
                          @ModelAttribute PassengerDto passengerDto,
                          @RequestParam(required = false) Boolean round,
                          @RequestParam(required = false) Boolean going,
                          @RequestParam(required = false) Boolean coming,
                          @RequestParam(required = false) String room1,
                          @RequestParam(required = false) String room2,
                          @RequestParam(required = false) String room3,
                          @RequestParam(required = false) String room4,
                          @RequestParam(required = false) String room5,
                          Model model) {

        log.info("시발 = {}", seatDto);
        log.info("시발 = {}", passengerDto);
        log.info("시발 = {}", seatDto.howManyOccupied());
        log.info("시발 = {}", beforeOccupied);
        System.out.println("beforeOccupied = " + beforeOccupied);
        System.out.println("passengerDto.howManyOccupied() = " + passengerDto.howManyOccupied());

        if(round == Boolean.TRUE && going == Boolean.TRUE) {
            if (room1 != null) {
                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(0);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("round", true);
                model.addAttribute("going", true);

                beforeOccupied = seatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);
                roomNo = 0;

                return "chooseSeat";
            }

            if (room2 != null) {
                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(1);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("round", true);
                model.addAttribute("going", true);

                beforeOccupied = seatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);
                roomNo = 1;

                return "chooseSeat";
            }

            if (room3 != null) {
                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(2);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("round", true);
                model.addAttribute("going", true);

                beforeOccupied = seatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);
                roomNo = 2;

                return "chooseSeat";
            }

            if (room4 != null) {
                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(3);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("round", true);
                model.addAttribute("going", true);

                beforeOccupied = seatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);
                roomNo = 3;

                return "chooseSeat";
            }

            if (room5 != null) {
                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(4);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("round", true);
                model.addAttribute("going", true);

                beforeOccupied = seatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);
                roomNo = 4;

                return "chooseSeat";
            }

            if(seatDto.howManyOccupied() - beforeOccupied != passengerDto.howManyOccupied()) {

                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(roomNo);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("passengerNumberNotSame", true);
                model.addAttribute("round", true);
                model.addAttribute("going", true);
                model.addAttribute("beforeOccupied", beforeOccupied);

                return "chooseSeat";
            }

            Long deployId = deployForm.getDeployIdOfComing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
            KtxRoom ktxRoom = ktxRooms.get(roomNo);

            seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
            model.addAttribute("seatDto", seatDto);
            model.addAttribute("round", true);
            model.addAttribute("coming", true);
            model.addAttribute("beforeOccupied", beforeOccupied);

            return "chooseSeat";
        }

        if(round == Boolean.TRUE && coming == Boolean.TRUE) {
            if (room1 != null) {
                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(0);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                beforeOccupied = seatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);
                roomNo = 0;

                return "chooseSeat";
            }

            if (room2 != null) {
                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(1);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                beforeOccupied = seatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);
                roomNo = 1;

                return "chooseSeat";
            }

            if (room3 != null) {
                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(2);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                beforeOccupied = seatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);
                roomNo = 2;

                return "chooseSeat";
            }

            if (room4 != null) {
                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(3);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                beforeOccupied = seatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);
                roomNo = 3;

                return "chooseSeat";
            }

            if (room5 != null) {
                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(4);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                beforeOccupied = seatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);
                roomNo = 4;

                return "chooseSeat";
            }

            if(seatDto.howManyOccupied() - beforeOccupied != passengerDto.howManyOccupied()) {
                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
                KtxRoom ktxRoom = ktxRooms.get(roomNo);

                seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
                model.addAttribute("seatDto", seatDto);
                model.addAttribute("passengerNumberNotSame", true);
                model.addAttribute("round", true);
                model.addAttribute("coming", true);
                model.addAttribute("beforeOccupied", beforeOccupied);
                return "chooseSeat";
            }

            return "temp";
        }

        if (room1 != null) {
            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
            KtxRoom ktxRoom = ktxRooms.get(0);

            seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
            model.addAttribute("seatDto", seatDto);
            model.addAttribute("going", true);

            beforeOccupied = seatDto.howManyOccupied();
            model.addAttribute("beforeOccupied", beforeOccupied);
            roomNo = 0;

            return "chooseSeat";
        }

        if (room2 != null) {
            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
            KtxRoom ktxRoom = ktxRooms.get(1);

            seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
            model.addAttribute("seatDto", seatDto);
            model.addAttribute("going", true);

            beforeOccupied = seatDto.howManyOccupied();
            model.addAttribute("beforeOccupied", beforeOccupied);
            roomNo = 1;

            return "chooseSeat";
        }

        if (room3 != null) {
            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
            KtxRoom ktxRoom = ktxRooms.get(2);

            seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
            model.addAttribute("seatDto", seatDto);
            model.addAttribute("going", true);

            beforeOccupied = seatDto.howManyOccupied();
            model.addAttribute("beforeOccupied", beforeOccupied);
            roomNo = 2;

            return "chooseSeat";
        }

        if (room4 != null) {
            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
            KtxRoom ktxRoom = ktxRooms.get(3);

            seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
            model.addAttribute("seatDto", seatDto);
            model.addAttribute("going", true);

            beforeOccupied = seatDto.howManyOccupied();
            model.addAttribute("beforeOccupied", beforeOccupied);
            roomNo = 3;

            return "chooseSeat";
        }

        if (room5 != null) {
            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
            KtxRoom ktxRoom = ktxRooms.get(4);

            seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
            model.addAttribute("seatDto", seatDto);
            model.addAttribute("going", true);

            beforeOccupied = seatDto.howManyOccupied();
            model.addAttribute("beforeOccupied", beforeOccupied);
            roomNo = 4;

            return "chooseSeat";
        }

        if(seatDto.howManyOccupied() - beforeOccupied != passengerDto.howManyOccupied()) {
            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtx(ktx);
            KtxRoom ktxRoom = ktxRooms.get(roomNo);

            seatDto = ktxSeatService.findDtoByKtxRoom(ktxRoom);
            model.addAttribute("seatDto", seatDto);
            model.addAttribute("passengerNumberNotSame", true);
            model.addAttribute("going", true);
            model.addAttribute("beforeOccupied", beforeOccupied);
            return "chooseSeat";
        }

        return "temp";
    }
}
