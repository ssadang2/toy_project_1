package toy.ktx.domain;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Data
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fee;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "deploy_id")
    private Deploy deploy;

    @OneToOne(fetch = LAZY, mappedBy = "reservation")
    private Passenger passenger;

    //편의 메소드
    public void savePassenger(Passenger passenger) {
        passenger.setReservation(this);
        this.passenger = passenger;
    }
}
