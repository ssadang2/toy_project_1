package toy.ktx.domain.ktx;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "ktx_seat")
public class KtxSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "room_id")
    private KtxRoom ktxRoom;

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

    public KtxSeat() {
    }

    public KtxSeat(KtxRoom ktxRoom, Boolean k1A, Boolean k2A, Boolean k3A, Boolean k4A, Boolean k5A, Boolean k6A, Boolean k7A, Boolean k8A, Boolean k9A, Boolean k10A, Boolean k11A, Boolean k12A, Boolean k31A, Boolean k14A, Boolean k1B, Boolean k2B, Boolean k3B, Boolean k4B, Boolean k5B, Boolean k6B, Boolean k7B, Boolean k8B, Boolean k9B, Boolean k10B, Boolean k11B, Boolean k12B, Boolean k31B, Boolean k14B, Boolean k1C, Boolean k2C, Boolean k3C, Boolean k4C, Boolean k5C, Boolean k6C, Boolean k7C, Boolean k8C, Boolean k9C, Boolean k10C, Boolean k11C, Boolean k12C, Boolean k31C, Boolean k14C, Boolean k1D, Boolean k2D, Boolean k3D, Boolean k4D, Boolean k5D, Boolean k6D, Boolean k7D, Boolean k8D, Boolean k9D, Boolean k10D, Boolean k11D, Boolean k12D, Boolean k31D, Boolean k14D) {
        this.ktxRoom = ktxRoom;
        this.k1A = k1A;
        this.k2A = k2A;
        this.k3A = k3A;
        this.k4A = k4A;
        this.k5A = k5A;
        this.k6A = k6A;
        this.k7A = k7A;
        this.k8A = k8A;
        this.k9A = k9A;
        this.k10A = k10A;
        this.k11A = k11A;
        this.k12A = k12A;
        this.k31A = k31A;
        this.k14A = k14A;
        this.k1B = k1B;
        this.k2B = k2B;
        this.k3B = k3B;
        this.k4B = k4B;
        this.k5B = k5B;
        this.k6B = k6B;
        this.k7B = k7B;
        this.k8B = k8B;
        this.k9B = k9B;
        this.k10B = k10B;
        this.k11B = k11B;
        this.k12B = k12B;
        this.k31B = k31B;
        this.k14B = k14B;
        this.k1C = k1C;
        this.k2C = k2C;
        this.k3C = k3C;
        this.k4C = k4C;
        this.k5C = k5C;
        this.k6C = k6C;
        this.k7C = k7C;
        this.k8C = k8C;
        this.k9C = k9C;
        this.k10C = k10C;
        this.k11C = k11C;
        this.k12C = k12C;
        this.k31C = k31C;
        this.k14C = k14C;
        this.k1D = k1D;
        this.k2D = k2D;
        this.k3D = k3D;
        this.k4D = k4D;
        this.k5D = k5D;
        this.k6D = k6D;
        this.k7D = k7D;
        this.k8D = k8D;
        this.k9D = k9D;
        this.k10D = k10D;
        this.k11D = k11D;
        this.k12D = k12D;
        this.k31D = k31D;
        this.k14D = k14D;
    }
}
