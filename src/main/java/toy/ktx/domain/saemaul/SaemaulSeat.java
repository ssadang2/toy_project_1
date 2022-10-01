package toy.ktx.domain.saemaul;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "saemaul_seat")
public class SaemaulSeat {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private SaemaulRoom saemaulRoom;

    private Boolean k1A;
    private Boolean k2A;
    private Boolean k3A;
    private Boolean k4A;
    private Boolean k5A;
    private Boolean k6A;
    private Boolean k7A;
    private Boolean k8A;
    private Boolean k9A;
    private Boolean k10A;
    private Boolean k11A;
    private Boolean k12A;
    private Boolean k31A;
    private Boolean k14A;

    private Boolean k1B;
    private Boolean k2B;
    private Boolean k3B;
    private Boolean k4B;
    private Boolean k5B;
    private Boolean k6B;
    private Boolean k7B;
    private Boolean k8B;
    private Boolean k9B;
    private Boolean k10B;
    private Boolean k11B;
    private Boolean k12B;
    private Boolean k31B;
    private Boolean k14B;

    private Boolean k1C;
    private Boolean k2C;
    private Boolean k3C;
    private Boolean k4C;
    private Boolean k5C;
    private Boolean k6C;
    private Boolean k7C;
    private Boolean k8C;
    private Boolean k9C;
    private Boolean k10C;
    private Boolean k11C;
    private Boolean k12C;
    private Boolean k31C;
    private Boolean k14C;

    private Boolean k1D;
    private Boolean k2D;
    private Boolean k3D;
    private Boolean k4D;
    private Boolean k5D;
    private Boolean k6D;
    private Boolean k7D;
    private Boolean k8D;
    private Boolean k9D;
    private Boolean k10D;
    private Boolean k11D;
    private Boolean k12D;
    private Boolean k31D;
    private Boolean k14D;
}
