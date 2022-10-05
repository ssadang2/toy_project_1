package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import toy.ktx.domain.Member;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.dto.LoginForm;
import toy.ktx.service.LoginService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/sign-in")
    public String getLoginPage(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "loginPage";
    }

    @PostMapping("/sign-in")
    public String completeLogin(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                                @RequestParam(defaultValue = "/") String redirectURL,
                                BindingResult bindingResult,
                                HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "loginPage";
        }

        Member member = loginService.doLogin(loginForm.getLoginId(), loginForm.getPassword());
        if(member == null) {
            bindingResult.reject("denied", "아이디 또는 비밀번호가 올바르지 않습니다.");
            log.info("errors={}", bindingResult);
            return "loginPage";
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
        return "redirect:" + redirectURL;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
