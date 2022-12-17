package toy.ktx.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "train")
@DiscriminatorColumn
@Data
@ToString(exclude = "deploy")
//toString stackOverFlow 막으려고
//아니면 @data를 빼버려도 될 듯
public class Train {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trainName;

//  원래는 양방향 ㄴㄴ인데, entity 탐색의 유연성을 위해 살리는 게 나을 듯 eager 쿼리 나가봤자 하나라서 더 나은 trade off라고 생각
//  결국엔 지워져야 할 듯
// 알고 보니 하나 찌르는 거 아니면 상관 없음 잘 쓰면 굳이 안 없애도 될 듯
    @OneToOne(mappedBy = "train", fetch = FetchType.LAZY)
    private Deploy deploy;

    public Train() {
    }

    public Train(String trainName) {
        this.trainName = trainName;
    }
}
