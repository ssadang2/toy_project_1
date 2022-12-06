package toy.ktx.domain.ktx;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
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

    @OneToOne(mappedBy = "ktxRoom", fetch = FetchType.LAZY)
    private KtxSeat ktxSeat;

    public KtxRoom() {
    }

    public KtxRoom(String roomName, Ktx ktx, Grade grade) {
        this.roomName = roomName;
        this.ktx = ktx;
        this.grade = grade;
    }

    public String isFull() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.convertValue(ktxSeat, Map.class);
        for (Object o : map.keySet()) {
            if (map.get(o).equals(Boolean.FALSE)) {
                return null;
            }
        }
        return this.getRoomName();
    }

    public Long howManyRemain() {
        ObjectMapper objectMapper = new ObjectMapper();
        Long reamin = Long.valueOf(0);
        Map map = objectMapper.convertValue(ktxSeat, Map.class);
        for (Object o : map.keySet()) {
            if (map.get(o).equals(Boolean.FALSE)) {
                reamin += 1;
            }
        }
        return reamin;
    }
}
