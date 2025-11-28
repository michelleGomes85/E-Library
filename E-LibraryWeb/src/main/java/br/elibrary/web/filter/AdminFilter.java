package br.elibrary.web.filter;

import java.io.IOException;

import br.elibrary.model.User;
import br.elibrary.model.enuns.Rules;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest requestArg, ServletResponse responseArg, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) requestArg;
        HttpServletResponse response = (HttpServletResponse) responseArg;

        User user = (User) request.getSession().getAttribute("loggedUser");

        if (user == null || user.getRules() != Rules.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/access-denied.xhtml");
            return;
        }

        chain.doFilter(requestArg, responseArg);
    }
}
