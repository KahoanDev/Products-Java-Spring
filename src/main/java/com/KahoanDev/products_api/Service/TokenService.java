package com.KahoanDev.products_api.Service;

import com.KahoanDev.products_api.Config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProperties jwtProperties;

    // -------------------------------------------------------------------------
    // GERAÇÃO
    // -------------------------------------------------------------------------

    public String gerarToken(UserDetails userDetails){
        var agora = Instant.now();
        var expiracao = agora.plusMillis(jwtProperties.expiration());

        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuer(jwtProperties.issuer())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .claim("authorities", authorities)   // roles no token
                .signWith(getSecretKey())
                .compact();
    }

    // -------------------------------------------------------------------------
    // VALIDAÇÃO
    // -------------------------------------------------------------------------

    public Optional<String> extrairSubject(String token) {
        try {
            Claims claims = parsearClaims(token);
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public List<String> extrairAuthorities(String token) {
        try {
            Claims claims = parsearClaims(token);
            Object raw = claims.get("authorities");
            if (raw instanceof List<?> lista) {
                return lista.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .toList();
            }
            return List.of();
        } catch (JwtException | IllegalArgumentException e) {
            return List.of();
        }
    }

    public boolean tokenValido(String token) {
        return extrairSubject(token).isPresent();
    }

    // -------------------------------------------------------------------------
    // PRIVADO
    // -------------------------------------------------------------------------

    private Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8)
        );
    }
}
