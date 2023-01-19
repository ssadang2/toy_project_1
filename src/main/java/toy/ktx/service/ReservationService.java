package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Member;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.dto.SignUpForm;
import toy.ktx.domain.dto.api.ReservationDto;
import toy.ktx.domain.dto.api.ReservationDto2;
import toy.ktx.domain.dto.api.ReservationWithMemberDeployTrainDto;
import toy.ktx.repository.ReservationRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Transactional
    public void saveReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public List<Reservation> findByMember(Member member) {
        return reservationRepository.findByMember(member);
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    public Optional<Reservation> getReservationToTrainByIdWithFetch(Long id) {
        return reservationRepository.getReservationToTrainByIdWithFetch(id);
    }

    public Page<Reservation> findAll(Pageable pageable) {
        return reservationRepository.findAll(pageable);
    }

    public Page<ReservationDto> findAllReservationDtoBy(Pageable pageable) {
        return reservationRepository.findAllReservationDtoBy(pageable);
    }

    public List<ReservationDto2> findAllReservationDto2ById(List<Long> ids) {
        return reservationRepository.findAllReservationDto2ById(ids);
    }

    public Page<Reservation> findAllReservationWithMemberDeployTrainFetch(Pageable pageable) {
        return reservationRepository.findAllReservationWithMemberDeployTrainFetch(pageable);
    }

    public Page<ReservationWithMemberDeployTrainDto> findAllReservationDtoWithMemberDeployTrain(Pageable pageable) {
        return reservationRepository.findAllReservationDtoWithMemberDeployTrain(pageable);
    }
}
