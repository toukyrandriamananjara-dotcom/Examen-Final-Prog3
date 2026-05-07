package com.collectivity.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class ApiKeyFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyFilter.class);
    private static final String API_KEY_HEADER = "x-api-key";

    @Value("${api.key}")
    private String expectedApiKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Ne pas vérifier la clé sur le endpoint d'erreur par défaut (évite les boucles)
        String path = httpRequest.getRequestURI();
        if (path.equals("/error")) {
            chain.doFilter(request, response);
            return;
        }

        String apiKey = httpRequest.getHeader(API_KEY_HEADER);

        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            logger.warn("Tentative d'accès non autorisé depuis {} - clé API manquante ou invalide",
                    httpRequest.getRemoteAddr());
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"message\": \"Bad credentials\"}");
            return;
        }

        // Clé valide, poursuivre la chaîne
        chain.doFilter(request, response);
    }
}