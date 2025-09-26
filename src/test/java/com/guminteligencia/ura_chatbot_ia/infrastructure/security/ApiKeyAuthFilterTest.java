package com.guminteligencia.ura_chatbot_ia.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthFilterTest {

    @InjectMocks
    ApiKeyAuthFilter filter;

    @Mock
    HttpServletRequest req;

    @Mock
    HttpServletResponse res;

    @Mock
    FilterChain chain;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(filter, "validApiKey", "CHAVE-TESTE-123");
    }

    @Test
    void missingKeyReturns401() throws Exception {
        when(req.getRequestURI()).thenReturn("/api/qualquer");
        when(req.getHeader("X-API-KEY")).thenReturn(null);

        filter.doFilter(req, res, chain);

        verify(res).sendError(HttpServletResponse.SC_UNAUTHORIZED, "API key missing or invalid");
        verify(chain, never()).doFilter(any(), any());
    }


    @Test
    void invalidKeyReturns401() throws Exception {
        when(req.getRequestURI()).thenReturn("/api/qualquer");
        when(req.getHeader("X-API-KEY")).thenReturn("WRONG");
        filter.doFilter(req, res, chain);
        verify(res).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "API key missing or invalid");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void validKeyContinues() throws Exception {
        when(req.getRequestURI()).thenReturn("/api/qualquer");
        when(req.getHeader("X-API-KEY")).thenReturn("CHAVE-TESTE-123");
        filter.doFilter(req, res, chain);
        verify(chain).doFilter(req, res);
        verify(res, never()).sendError(anyInt(), anyString());
    }
}