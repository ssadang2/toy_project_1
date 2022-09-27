package toy.ktx.domain;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "train")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Data
public class Train {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "departure_time")
    @NotBlank
    private LocalDateTime departureTime;

    @Column(name = "arrival_time")
    @NotBlank
    private LocalDateTime arrivalTime;

    @Column(name = "departure_place")
    @NotBlank
    private String departurePlace;

    @Column(name = "arrival_place")
    @NotBlank
    private String arrivalPlace;

    @NotBlank
    private Long charge;

    @OneToOne(mappedBy = "train")
    private Reservation reservation;

}
