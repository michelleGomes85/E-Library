package br.elibrary.web.filter;

import java.io.IOException;

import br.elibrary.dto.UserDTO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter({"/user/*", "/admin/*"}) 
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest requestArg, ServletResponse responseArg, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) requestArg;
        HttpServletResponse response = (HttpServletResponse) responseArg;

        UserDTO user = (UserDTO) request.getSession().getAttribute("loggedUser");

        if (user == null) {  
            response.sendRedirect(request.getContextPath() + "/login.xhtml");
            return;
        }

        chain.doFilter(requestArg, responseArg);
    }
}
