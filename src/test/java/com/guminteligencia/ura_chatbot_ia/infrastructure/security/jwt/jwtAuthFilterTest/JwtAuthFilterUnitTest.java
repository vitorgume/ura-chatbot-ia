package com.guminteligencia.ura_chatbot_ia.infrastructure.security.jwt.jwtAuthFilterTest;

import com.guminteligencia.ura_chatbot_ia.infrastructure.security.jwt.JwtAuthFilter;
import com.guminteligencia.ura_chatbot_ia.infrastructure.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterUnitTest {

    @Mock
    JwtUtil jwtUtil;

    @Mock
    HttpServletRequest req;

    @Mock
    HttpServletResponse res;

    @Mock
    FilterChain chain;

    JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthFilter(jwtUtil);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // shouldNotFilter: OPTIONS
    @Test
    void optionsBypassaFilter() throws ServletException, IOException {
        when(req.getMethod()).thenReturn("OPTIONS");

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        verifyNoInteractions(jwtUtil);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // shouldNotFilter: path público
    @Test
    void pathsPublicosBypassamFilter() throws ServletException, IOException {
        when(req.getMethod()).thenReturn("GET");
        when(req.getRequestURI()).thenReturn("/healthz/ping");

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        verifyNoInteractions(jwtUtil);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // doFilterInternal: sem Authorization
    @Test
    void semAuthorizationNaoAutentica() throws ServletException, IOException {
        when(req.getMethod()).thenReturn("GET");
        when(req.getRequestURI()).thenReturn("/api/protegido");
        when(req.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        verifyNoInteractions(jwtUtil);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // doFilterInternal: prefixo inválido
    @Test
    void authorizationComPrefixoInvalidoNaoAutentica() throws ServletException, IOException {
        when(req.getMethod()).thenReturn("GET");
        when(req.getRequestURI()).thenReturn("/api/protegido");
        when(req.getHeader("Authorization")).thenReturn("Basic abc123");

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        verifyNoInteractions(jwtUtil);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // doFilterInternal: Bearer, token inválido
    @Test
    void bearerTokenInvalidoLimpaContexto() throws ServletException, IOException {
        when(req.getMethod()).thenReturn("GET");
        when(req.getRequestURI()).thenReturn("/api/protegido");
        when(req.getHeader("Authorization")).thenReturn("Bearer tok");
        when(jwtUtil.isTokenValid("tok")).thenReturn(false);

        filter.doFilter(req, res, chain);

        verify(jwtUtil).isTokenValid("tok");
        verify(chain).doFilter(req, res);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // doFilterInternal: Bearer, token válido
    @Test
    void bearerTokenValidoAutentica() throws ServletException, IOException {
        when(req.getMethod()).thenReturn("GET");
        when(req.getRequestURI()).thenReturn("/api/protegido");
        when(req.getHeader("Authorization")).thenReturn("Bearer good");
        when(jwtUtil.isTokenValid("good")).thenReturn(true);
        when(jwtUtil.extractUsername("good")).thenReturn("user1");

        filter.doFilter(req, res, chain);

        verify(jwtUtil).isTokenValid("good");
        verify(jwtUtil).extractUsername("good");
        verify(chain).doFilter(req, res);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(auth.getName()).isEqualTo("user1");
        assertThat(auth.getAuthorities()).isEqualTo(Collections.emptyList());
        assertThat(auth.getDetails()).isNotNull(); // detalhes do request aplicados
    }

    // doFilterInternal: exceção no jwtUtil
    @Test
    void excecaoNoJwtUtilLimpaContextoEProssegue() throws ServletException, IOException {
        when(req.getMethod()).thenReturn("GET");
        when(req.getRequestURI()).thenReturn("/api/protegido");
        when(req.getHeader("Authorization")).thenReturn("Bearer boom");
        when(jwtUtil.isTokenValid("boom")).thenThrow(new RuntimeException("erro qualquer"));

        filter.doFilter(req, res, chain);

        verify(jwtUtil).isTokenValid("boom");
        verify(chain).doFilter(req, res);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
