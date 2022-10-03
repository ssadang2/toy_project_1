package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy.ktx.domain.Member;
import toy.ktx.domain.dto.SignUpForm;
import toy.ktx.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Optional<Member> findByLoginId(String loginId){
        return memberRepository.findByLoginId(loginId);
    }

    public void saveMember(SignUpForm signUpForm) {
        Member member = new Member();

        member.setLoginId(signUpForm.getLoginId());
        member.setPassword(signUpForm.getPassword());
        member.setName(signUpForm.getName());
        member.setAge(signUpForm.getAge());

        memberRepository.save(member);
    }
}
