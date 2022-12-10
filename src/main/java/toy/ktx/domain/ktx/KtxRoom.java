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

//    @BatchSize(size = 100)
//    배치 사이즈 의미가 없는 게 1:1 관계임
//    양반향 OneToOne은 query시 문제가 많기 때문에 안 쓰는 게 좋을 듯
    @OneToOne(mappedBy = "ktxRoom", fetch = FetchType.LAZY)
    private KtxSeat ktxSeat;

    public KtxRoom() {
    }

    public KtxRoom(String roomName, Ktx ktx, Grade grade) {
        this.roomName = roomName;
        this.ktx = ktx;
        this.grade = grade;
    }

//    public Boolean howManyRemain(Integer passengers) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        Long remain = Long.valueOf(0);
//        Map map = objectMapper.convertValue(this.getKtxSeat(), Map.class);
//        for (Object o : map.keySet()) {
//            if (map.get(o).equals(Boolean.FALSE)) {
//                remain += 1;
//            }
//        }
//        if (remain >= passengers) {
//            return Boolean.TRUE;
//        }
//        return null;
//    }
}
