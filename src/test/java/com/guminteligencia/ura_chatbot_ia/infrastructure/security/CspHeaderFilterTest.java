package com.guminteligencia.ura_chatbot_ia.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CspHeaderFilterTest {
    CspHeaderFilter filter = new CspHeaderFilter();

    @Mock
    HttpServletRequest req;

    @Mock
    HttpServletResponse res;

    @Mock
    FilterChain chain;

    @Test
    void setsCspHeaderAndContinuesChain() throws Exception {
        filter.doFilter(req, res, chain);
        verify(res).setHeader("Content-Security-Policy",
                "default-src 'self'; script-src 'self';");
        verify(chain).doFilter(req, res);
    }

}