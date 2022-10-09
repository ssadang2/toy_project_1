package toy.ktx.web.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import toy.ktx.domain.constant.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

public class signInInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession();

        if((session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null)
                && requestURI.equals("/schedule")) {

            response.sendRedirect("/sign-in");
            return false;
        }

        if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            response.sendRedirect("/sign-in?redirectURL=" + requestURI);
            return false;
        }

        return true;
    }
}
