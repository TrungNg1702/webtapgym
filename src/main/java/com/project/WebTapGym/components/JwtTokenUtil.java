package com.project.WebTapGym.components;

import com.project.WebTapGym.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(User user)
    {
        Map<String, Object> claims = new HashMap<>();
//        this.generateSecretKey();
        claims.put("phone", user.getPhone());
        claims.put("role", user.getRole().getRoleName());
        try{
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhone())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration *1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        } catch (Exception e) {
             throw new InvalidParameterException("Cannot generate token, error:" + e.getMessage());
//            return null;
        }
    }

    private Key getSignInKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // Keys.hmacShaKeyFor(Decoders.BASE64.decode("9PjXnUkHfkkomKMqwLgDGskYtd04OJWrGRhXn6dnzVo="));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateSecretKey()
    {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String key = Encoders.BASE64.encode(bytes);
        return key;
    }

    private Claims extractAllClaims(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token)
    {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
}
