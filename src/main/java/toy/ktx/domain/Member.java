package toy.ktx.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "member")
public class Member {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "login_id", unique = true)
    private String loginId;

    private String password;

    private String name;

    private Long age;

    @OneToMany(mappedBy = "member")
    private List<Reservation> reservations = new ArrayList<>();
}
