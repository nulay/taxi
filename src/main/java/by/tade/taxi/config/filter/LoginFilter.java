package by.tade.taxi.config.filter;

import by.tade.taxi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
@Order(1)
public class LoginFilter implements Filter {
    private final UserService userService;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        if (userService.getUserSession().getLogin() == null &&
                !servletRequest.getRequestURI().contains("/index") &&
                !servletRequest.getRequestURI().contains("/registration")) {

            String encodedRedirectURL = ((HttpServletResponse) response).encodeRedirectURL(
                    servletRequest.getContextPath() + "/index");

//            servletResponse.setStatus(HttpStatus.SC_TEMPORARY_REDIRECT);
            servletResponse.setStatus(307);
            servletResponse.setHeader("Location", encodedRedirectURL);
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    // other methods
}

