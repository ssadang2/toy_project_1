package toy.ktx.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Member;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void save() {

        Member member = new Member("a", "1234", "eric", Long.valueOf(26));
        memberRepository.save(member);

        Member member1 = memberRepository.findById(member.getId()).get();

        assertThat(member).isEqualTo(member1);
    }
}