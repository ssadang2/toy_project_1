package toy.ktx.domain;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "deploy")
public class Deploy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "deploy")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "train_id")
    private Train train;
}
