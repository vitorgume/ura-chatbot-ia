package com.guminteligencia.ura_chatbot_ia.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    public final String HEADER_NAME = "X-API-KEY";

    @Value("${ura-chatbot-ia.apikey}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String apiKey = req.getHeader(HEADER_NAME);
        if (apiKey == null || !apiKey.equals(validApiKey)) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API key missing or invalid");
            return;
        }

        chain.doFilter(req, res);
    }
}
