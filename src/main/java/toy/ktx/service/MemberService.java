package toy.ktx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.ktx.domain.Member;
import toy.ktx.domain.dto.SignUpForm;
import toy.ktx.domain.dto.api.MemberWithReservationDto;
import toy.ktx.domain.enums.Authorizations;
import toy.ktx.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Page<Member> findAllByAuthorizations(Authorizations authorizations, Pageable pageable) {
        return memberRepository.findAllByAuthorizations(authorizations, pageable);
    }

    public Page<MemberWithReservationDto> findAllMemberDtosByAuthorizations(Authorizations authorizations, Pageable pageable) {
        return memberRepository.findAllMemberDtosByAuthorizations(authorizations, pageable);
    }

    public Optional<Member> findByLoginId(String loginId){
        return memberRepository.findByLoginId(loginId);
    }

    @Transactional
    public void dtoToSaveMember(SignUpForm signUpForm) {
        Member member = new Member();

        member.setLoginId(signUpForm.getLoginId());
        member.setPassword(signUpForm.getPassword());
        member.setName(signUpForm.getName());
        member.setAge(signUpForm.getAge());

        memberRepository.save(member);
    }

    @Transactional
    public void save(Member member) {
        memberRepository.save(member);
    }

    public List<Member> findAllMembersWithReservationFetch() {
        return memberRepository.findAllMembersWithReservationFetch();
    }

    public Page<MemberWithReservationDto> findAllMemberDtos(Pageable pageable) {
        return memberRepository.findAllMemberDtos(pageable);
    }
}
