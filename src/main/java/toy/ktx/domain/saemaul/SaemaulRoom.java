package toy.ktx.domain.saemaul;

import lombok.Data;
import toy.ktx.domain.enums.Grade;

import javax.persistence.*;

@Entity
@Data
@Table(name = "saemaul_room")
public class SaemaulRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saemaul_id")
    private Saemaul saemaul;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "seat_id")
    private SaemaulSeat saemaulSeat;


    public SaemaulRoom() {
    }

    public SaemaulRoom(String roomName, Saemaul saemaul, SaemaulSeat saemaulSeat) {
        this.roomName = roomName;
        this.saemaul = saemaul;
        this.saemaulSeat = saemaulSeat;
    }
}
