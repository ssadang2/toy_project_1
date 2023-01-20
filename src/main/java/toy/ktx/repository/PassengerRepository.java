package toy.ktx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.ktx.domain.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
