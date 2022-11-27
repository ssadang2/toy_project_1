package toy.ktx.domain;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import toy.ktx.domain.enums.Grade;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Data
@Table(name = "reservation")
@ToString(exclude = "{member, passenger}")
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    private String seats;

    private String roomName;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    //편의 메서드
    //연관관계 주인이 바뀐 시점에서 굳이 편의 메서드 쓸 필요없음
    public void savePassenger(Passenger passenger) {
        passenger.setReservation(this);
        this.passenger = passenger;
    }
}
