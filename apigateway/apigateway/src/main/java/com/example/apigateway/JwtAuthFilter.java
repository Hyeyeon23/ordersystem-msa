package com.example.apigateway;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Log4j2
public class JwtAuthFilter implements GlobalFilter {


    @Value("${jwt.secretKey}")
    private String sercretKey;

    private static final List<String> ALLOWED_PATHS = List.of(
            "/member/create",
            "/member/doLogin",
            "/member/refresh-token",
            "/product/list"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("token 검증 시작");
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String path = exchange.getRequest().getURI().getRawPath();
        log.info(path);

        // 인증이 필요 없는 경로는 필터를 통과
        if(ALLOWED_PATHS.contains(path)){
            return chain.filter(exchange);
        }

        try {
            if(bearerToken == null || !bearerToken.startsWith("Bearer ")){
                throw new IllegalArgumentException("token 관련 예외 발생");
            }

            String token = bearerToken.substring(7);


            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(sercretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            ServerWebExchange modifiedExchange = exchange.mutate()
//                    X-룰 붙이는 것은 custom 헤더라는 널리 쓰이는 관용표현
//                    서비스 모듈에서 기존처럼 톸느에 있는 정보를 이용해서 서비스 로직을 처리할 수 있게
                    .request(builder -> builder
                            .header("X-User-Id", userId)
                            .header("X-User-Role", "ROLE_"+role)
                    ).build();

            return chain.filter(modifiedExchange);
        }catch (IllegalArgumentException | MalformedJwtException | ExpiredJwtException | SignatureException| UnsupportedJwtException e){

            e.printStackTrace();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return  exchange.getResponse().setComplete();

        }


    }
}
