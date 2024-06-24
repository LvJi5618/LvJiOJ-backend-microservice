package com.lvji.lvjiojbackendgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.domain.geo.GeoLocation;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String path = serverHttpRequest.getURI().getPath();
        // 判断用户请求路径中是否包含“\inner”
        if(antPathMatcher.match("/**/inner/**",path)){
            // 若请求中包含“/inner”,返回403
            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            serverHttpResponse.setStatusCode(HttpStatus.FORBIDDEN);
            // 填充响应,向用户返请求异常的原因
            DataBufferFactory dataBufferFactory = serverHttpResponse.bufferFactory();
            DataBuffer dataBuffer = dataBufferFactory.wrap("无权限".getBytes(StandardCharsets.UTF_8));
            // Mono:响应式对象的单元 Mono.just(T t):创建一个响应式对象
            return serverHttpResponse.writeWith(Mono.just(dataBuffer));
        }

        // todo 统一权限校验,可以使用 JWT Token 实现用户登录，在网关层面通过 token 获取登录信息，实现鉴权

        return chain.filter(exchange);

    }

    /**
     * 若有多个拦截器,当前拦截器的执行优先级最高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
