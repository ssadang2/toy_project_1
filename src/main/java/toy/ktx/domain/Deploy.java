package toy.ktx.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "deploy")
@Data
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

    @OneToOne(fetch = FetchType.LAZY)
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
