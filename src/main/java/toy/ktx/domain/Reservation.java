package toy.ktx.domain;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Data
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue
    private Long id;

    private Long fee;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "deploy_id")
    private Deploy deploy;

    @OneToOne(fetch = LAZY, mappedBy = "reservation")
    private Passenger passenger;
}
