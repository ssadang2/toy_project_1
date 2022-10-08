package toy.ktx.domain;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "deploy")
public class Deploy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "departure_time")
    @NotNull
    private LocalDateTime departureTime;

    @Column(name = "arrival_time")
    @NotNull
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

    public Deploy() {
    }

    public Deploy(LocalDateTime departureTime, LocalDateTime arrivalTime, String departurePlace, String arrivalPlace, Train train) {
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.departurePlace = departurePlace;
        this.arrivalPlace = arrivalPlace;
        this.train = train;
    }
}
