package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Passenger;
import toy.ktx.repository.PassengerRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassengerService {

    private final PassengerRepository passengerRepository;

    public void save(Passenger passenger) {
        passengerRepository.save(passenger);
    }
}
