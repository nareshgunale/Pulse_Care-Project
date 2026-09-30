package com.hms.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;

@Component
public class TokenFilter extends AbstractGatewayFilterFactory<TokenFilter.Config> {

    private final SecretKey key;

    public TokenFilter(@Value("${jwt.secret}") String secret) {
        super(Config.class);

        System.out.println("JWT Secret Loaded: " + (secret != null));

        this.key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            // Allow CORS preflight
            if ("OPTIONS".equals(exchange.getRequest().getMethod().name())) {
                return chain.filter(exchange);
            }

            String path = exchange.getRequest().getURI().getPath();

            System.out.println("PATH = " + path);

            // Public APIs
            if (path.contains("/user/login")
                    || path.contains("/user/register")
                    || path.startsWith("/api/ai/ask")
                    || (exchange.getRequest().getMethod().name().equals("GET")
                    && (path.equals("/api/hospitals")
                    || path.startsWith("/api/hospitals/")
                    || path.equals("/profile/doctor/all")))) {

                return chain.filter(exchange);
            }

            String authHeader =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            try {

                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username = claims.getSubject();

                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(builder -> builder
                                .header("X-User", username)
                                .header(HttpHeaders.AUTHORIZATION, authHeader))
                        .build();

                return chain.filter(mutatedExchange);

            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {
    }
}
//package com.hms.gateway.filter;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//
//import javax.crypto.SecretKey;
//import io.jsonwebtoken.io.Decoders;
//
//@Component
//public class TokenFilter extends AbstractGatewayFilterFactory<TokenFilter.Config> {
//
//
//    @Value("${jwt.secret}")
//    private String SECRET;
//
//    //SecretKey type + created once + explicit UTF-8
////    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
//
//    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
//
//    public TokenFilter() {
//        super(Config.class);
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//
//            if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
//                return chain.filter(exchange);
//            }
//
//            String path = exchange.getRequest().getURI().getPath();
//
//            System.out.println("PATH = " + path);
//
//            if (path.contains("/user/login") || path.contains("/user/register")) {
//                return chain.filter(exchange);
//            }
//
//            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//
//            String token = authHeader.substring(7);
//
//            try {
//                Claims claims = Jwts.parser()
//                        .verifyWith(key)          //reuse key, correct type
//                        .build()
//                        .parseSignedClaims(token)
//                        .getPayload();
//
//                //Forward user info to downstream services
//                String username = claims.getSubject();
//                ServerWebExchange mutatedExchange = exchange.mutate()
//                        .request(r -> r
//                                .header("X-User", username)
//                                .header(HttpHeaders.AUTHORIZATION, authHeader)
//                        )
//                        .build();
//
//                return chain.filter(mutatedExchange);
//
//            } catch (Exception e) {
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//        };
//    }
//
//    public static class Config {
//    }
//}
