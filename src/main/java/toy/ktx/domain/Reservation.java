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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    private String seats;

    private String roomName;

    @Enumerated(EnumType.STRING)
    private Grade grade;
}
