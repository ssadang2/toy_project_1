package toy.ktx.domain.mugunhwa;

import lombok.Data;
import toy.ktx.domain.enums.Grade;

import javax.persistence.*;


@Entity
@Data
@Table(name = "mugunghwa_room")
public class MugunghwaRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;

    @ManyToOne
    @JoinColumn(name = "mugunghwa_id")
    private Mugunghwa mugunghwa;

    @OneToOne
    @JoinColumn(name = "seat_id")
    private MugunghwaSeat mugunghwaSeat;

    public MugunghwaRoom() {
    }

    public MugunghwaRoom(String roomName, Mugunghwa mugunghwa, MugunghwaSeat mugunghwaSeat) {
        this.roomName = roomName;
        this.mugunghwa = mugunghwa;
        this.mugunghwaSeat = mugunghwaSeat;
    }
}
