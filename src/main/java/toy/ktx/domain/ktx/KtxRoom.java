package toy.ktx.domain.ktx;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;
import toy.ktx.domain.enums.Grade;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Table(name = "ktx_room")
@ToString(exclude = {"ktx", "ktxSeat"})
@Slf4j
public class KtxRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ktx_id")
    private Ktx ktx;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "seat_id")
    private KtxSeat ktxSeat;

    public KtxRoom() {
    }

    public KtxRoom(String roomName, Ktx ktx, Grade grade,  KtxSeat ktxSeat) {
        this.roomName = roomName;
        this.grade = grade;
        this.ktx = ktx;
        this.ktxSeat = ktxSeat;
    }
}
