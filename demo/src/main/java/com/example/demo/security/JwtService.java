package com.example.demo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.validity}")
    private Long jwtValidity;

    public String generateToken(UserDetails userDetails) {
        log.info("Generate jwt");
        Map<String, Object> claims = new HashMap<>();
        String commaSeparatedListOfAuthorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("authorities", commaSeparatedListOfAuthorities);

        return Jwts.builder().setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtValidity))
                .signWith(SignatureAlgorithm.HS256, jwtSecret).compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractAuthorities(String token) {
        Function<Claims, String> claimsListFunction = claims -> (String) claims.get("authorities");
        return extractClaim(token, claimsListFunction);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        if (claims != null)
            return claimsResolver.apply(claims);
        else return null;
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        } catch (MalformedJwtException e) {
            log.error("Unable to parse jwt.");
            throw e;
        } catch (ExpiredJwtException e) {
            log.error("Token expired.");
            throw e;
        }
    }
}
