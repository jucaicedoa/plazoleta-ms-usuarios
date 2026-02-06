package com.plazoleta.usuarios.infraestructure.out.security.filter;

import com.plazoleta.usuarios.domain.model.TokenClaims;
import com.plazoleta.usuarios.domain.spi.JwtProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";

    @Mock
    private JwtProviderPort jwtProviderPort;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    @AfterEach
    void limpiarSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deberiaContinuarCadenaSinAutenticacionCuandoNoHayHeaderAuthorization() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtProviderPort, never()).validarToken(anyString());
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void deberiaContinuarCadenaSinAutenticacionCuandoHeaderNoEsBearer() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlckBtYWlsLmNvbTpjbGF2ZQ==");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtProviderPort, never()).validarToken(anyString());
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void deberiaEstablecerAutenticacionEnContextoCuandoTokenEsValido() throws ServletException, IOException {
        // Arrange
        String token = "token.jwt.valido";
        String correo = "usuario@mail.com";
        String rol = "PROPIETARIO";
        TokenClaims claims = new TokenClaims(1, correo, rol, null);

        when(request.getHeader("Authorization")).thenReturn(BEARER_PREFIX + token);
        when(jwtProviderPort.validarToken(token)).thenReturn(Optional.of(claims));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtProviderPort).validarToken(token);
        verify(filterChain).doFilter(request, response);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(correo, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> (ROLE_PREFIX + rol).equals(a.getAuthority())));
    }

    @Test
    void deberiaContinuarSinAutenticacionCuandoTokenEsInvalido() throws ServletException, IOException {
        // Arrange
        String token = "token.invalido";
        when(request.getHeader("Authorization")).thenReturn(BEARER_PREFIX + token);
        when(jwtProviderPort.validarToken(token)).thenReturn(Optional.empty());

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtProviderPort).validarToken(token);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void deberiaUsarRolVacioCuandoClaimsTienenRolNull() throws ServletException, IOException {
        // Arrange
        String token = "token.jwt.valido";
        String correo = "sinrol@mail.com";
        TokenClaims claims = new TokenClaims(2, correo, null, null);

        when(request.getHeader("Authorization")).thenReturn(BEARER_PREFIX + token);
        when(jwtProviderPort.validarToken(token)).thenReturn(Optional.of(claims));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(correo, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> ROLE_PREFIX.equals(a.getAuthority())));
    }

    @Test
    void deberiaExtraerTokenSinEspaciosCuandoHeaderTieneEspaciosTrasBearer() throws ServletException, IOException {
        // Arrange
        String token = "mi.token.jwt";
        TokenClaims claims = new TokenClaims(1, "a@b.com", "EMPLEADO", null);
        when(request.getHeader("Authorization")).thenReturn(BEARER_PREFIX + "  " + token);
        when(jwtProviderPort.validarToken(eq(token))).thenReturn(Optional.of(claims));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert - el filter hace substring(BEARER_PREFIX.length()).trim(), así que pasa "  " + token
        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(jwtProviderPort).validarToken(tokenCaptor.capture());
        assertEquals(token, tokenCaptor.getValue().trim());
        verify(filterChain).doFilter(request, response);
    }
}