package toy.ktx.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "passenger")
public class Passenger {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer toddler;

    private Integer kids;

    private Integer adult;

    private Integer senior;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}
