package toy.ktx.domain.ktx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Data
@Slf4j
@Table(name = "ktx_seat")
@ToString(exclude = {"ktxRoom", "objectMapper"})
@Inheritance(strategy = InheritanceType.JOINED)
//join 전략에서는 빼면 dtype 안 들어감
@DiscriminatorColumn
public class KtxSeat {

    @Transient
    @JsonIgnore
    ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    //objectmapper 사용시 무분별한 객체 탐색 cut
//    @JsonIgnore
//    //양반향 OneToOne은 query시 문제가 많기 때문에 안 쓰는 게 좋을 듯
//    @OneToOne(mappedBy = "ktxSeat", fetch = FetchType.LAZY)
//    private KtxRoom ktxRoom;

    public KtxSeat() {
    }
}
