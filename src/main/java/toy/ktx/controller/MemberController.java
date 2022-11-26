package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import toy.ktx.domain.Member;
import toy.ktx.domain.dto.SignUpForm;
import toy.ktx.service.MemberService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/sign-up")
    public String getSignUpPage(@ModelAttribute SignUpForm SignUpForm) {
        return "signUpPage";
    }

    @PostMapping("/sign-up")
    public String completeSignUp(@Valid @ModelAttribute SignUpForm signUpForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "signUpPage";
        }

        Member member = memberService.findByLoginId(signUpForm.getLoginId()).orElse(null);
        if(member != null) {
            bindingResult.rejectValue("loginId", "dup", "중복된 로그인 아이디입니다.");
            return "signUpPage";
        }
        memberService.dtoToSaveMember(signUpForm);

        return "redirect:/";
    }
}
