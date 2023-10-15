package br.com.leonardofariaoliveira.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.leonardofariaoliveira.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var servLetPath = request.getServletPath();

        if(!servLetPath.startsWith("/tasks")){
            filterChain.doFilter(request, response);
        }

        var auth = request.getHeader("Authorization");

        var authEncoded = auth.substring("Basic".length()).trim();

        byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

        String[] credencials = new String(authDecoded).split(":");

        var userName = credencials[0];
        var userPassword = credencials[1];

        var hasUser = this.userRepository.findByUserName(userName);

        if(hasUser != null){
            response.sendError(401, "Usuário sem permissão");
        }

        var passwordVerify = BCrypt.verifyer().verify(userPassword.toCharArray(), hasUser.getPassword());
        if(passwordVerify.verified){
            request.setAttribute("user_id", hasUser.getId());
            filterChain.doFilter(request, response);
        }

        response.sendError(401, "Usuário sem permissão");

    }
}
