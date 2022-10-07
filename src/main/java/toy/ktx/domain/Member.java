package toy.ktx.domain;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;
import toy.ktx.domain.enums.Authorizations;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "member")
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", unique = true)
    private String loginId;

    private String password;

    private String name;

    private Long age;

    @Enumerated(EnumType.STRING)
    private Authorizations authorizations = Authorizations.USER;

    @OneToMany(mappedBy = "member")
    private List<Reservation> reservations = new ArrayList<>();

    public Member() {
    }

    public Member(String loginId, String password, String name, Long age) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.age = age;
    }
}
