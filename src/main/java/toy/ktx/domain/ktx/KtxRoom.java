package toy.ktx.domain.ktx;

import lombok.Data;
import toy.ktx.domain.enums.Grade;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "ktx_room")
public class KtxRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ktx_id")
    private Ktx ktx;

    @OneToOne(mappedBy = "ktxRoom", fetch = FetchType.LAZY)
    private KtxSeat ktxSeat;

    public KtxRoom() {
    }

    public KtxRoom(String roomName, Ktx ktx, Grade grade) {
        this.roomName = roomName;
        this.ktx = ktx;
        this.grade = grade;
    }
}
