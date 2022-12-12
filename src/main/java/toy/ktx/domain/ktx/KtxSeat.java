package toy.ktx.domain.ktx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.dto.projections.VipSeatDto;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

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

//    public void normalDtoToEntity(NormalSeatDto normalSeatDto) {
//        Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
//        try {
//            Class clazz = Class.forName("toy.ktx.domain.ktx.KtxSeat");
//            for (Object o : seatMap.keySet()) {
//                if ((boolean) seatMap.get(o) == true) {
//                    String temp = "setK" + ((String)o).substring(1);
//                    Method declaredMethod = clazz.getDeclaredMethod(temp, Boolean.class);
//                    declaredMethod.invoke(this, true);
//                }
//            }
//        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void vipDtoToEntity(VipSeatDto vipSeatDto) {
//        Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
//        try {
//            Class clazz = Class.forName("toy.ktx.domain.ktx.KtxSeat");
//            for (Object o : seatMap.keySet()) {
//                if ((boolean) seatMap.get(o) == true) {
//                    String temp = "setK" + ((String)o).substring(1);
//                    Method declaredMethod = clazz.getDeclaredMethod(temp, Boolean.class);
//                    declaredMethod.invoke(this, true);
//                }
//            }
//        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
