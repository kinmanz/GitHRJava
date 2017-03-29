package GitHR.config;

import GitHR.Services.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private MyServicePerSession myServicePerSession;


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Object token = request.getSession().getAttribute("token");

        System.out.println(myServicePerSession.getName());

        return true;
    }
}
