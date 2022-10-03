package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy.ktx.domain.Member;
import toy.ktx.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    public Member doLogin(String loginId, String password){
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password)).orElse(null);
    }
}
