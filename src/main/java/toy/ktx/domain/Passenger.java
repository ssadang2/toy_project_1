package toy.ktx.domain;

import lombok.Data;
import lombok.ToString;
import toy.ktx.domain.dto.PassengerDto;

import javax.persistence.*;

@Entity
@Data
@Table(name = "passenger")
@ToString(exclude = "reservation")
public class Passenger {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer toddler;

    private Integer kids;

    private Integer adult;

    private Integer senior;
}
