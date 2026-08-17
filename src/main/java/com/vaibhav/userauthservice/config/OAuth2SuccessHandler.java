package com.vaibhav.userauthservice.config;

import com.vaibhav.userauthservice.entity.User;
import com.vaibhav.userauthservice.repository.UserRepository;
import com.vaibhav.userauthservice.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String rawEmail = oAuth2User.getAttribute("email");
        String rawName = oAuth2User.getAttribute("name");

        if (rawEmail == null) {
            String login = oAuth2User.getAttribute("login");
            rawEmail = login + "@github.com";
            if (rawName == null) {
                rawName = login;
            }
        }

        final String email = rawEmail;
        final String name = rawName;

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(null);
            newUser.setRole("USER");
            return userRepository.save(newUser);
        });

        String token = jwtUtil.generateToken(user.getEmail());

        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
    }

}