package com.shampo.shampogateway;

import ch.qos.logback.classic.pattern.LineOfCallerConverter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
* 全局过滤
*/
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> IP_WHITE_LIST= Arrays.asList("127.0.0.1");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. 用户发送请求到API网关
        // 2. 请求日志
        ServerHttpRequest request= exchange.getRequest();
        log.info("请求唯一标识"+request.getId());
        log.info("请求路径"+request.getPath().value());
        log.info("请求方法"+request.getMethod());
        log.info("请求参数"+request.getQueryParams());
        String hostString = request.getLocalAddress().getHostString();
        log.info("请求来源地址"+hostString);
        log.info("请求来源地址"+request.getRemoteAddress());

        // 3. （黑白名单）
        ServerHttpResponse response = exchange.getResponse();

        if(!IP_WHITE_LIST.contains(hostString)){

            return handleNoAuth(response);
        }

        // 4. 用户鉴权（判断accessKey、secretKey是否合法）
        authenticate(request,response);

        // 5. 判断请求的模拟接口是否存在
        // todo 从数据库查询模拟接口是否存在，以及请求方法是否匹配，（校验请求参数）
        // todo 因为网关项目没有引入MyBatis等操作数据库的类库，如果该操作比较复杂，可以由backend增删改查项目提供接口，直接调用接口，不再重复写逻辑

        // 6. 请求转发、调用模拟接口
       // Mono<Void> filter=chain.filter(exchange);
        // log.info("响应"+response.getStatusCode());

        // 7. 响应日志
        log.info("custom global filter");
        Mono<Void> filter=handleResponse(exchange, chain);//异步调用
        log.info("skip the handleResponse()method and continue to execute the next method");
        return filter;
    }

    private Mono<Void> handleNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private Mono<Void> handleInvokeError(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                log.info(statusCode.toString());
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 8. todo 调用成功，接口调用次数 + 1 invokeCount
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        int a=1/0;
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                Mono<Void>result= chain.filter(exchange.mutate().response(decoratedResponse).build());
                log.info("fffffffffffffffffff");
                return  result;
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }


    private Mono<Void> authenticate(ServerHttpRequest request, ServerHttpResponse response){
        HttpHeaders headers = request.getHeaders();
        String accessKey=headers.getFirst("accessKey");
        String nonce=headers.getFirst("nonce");
        String timeStamp=headers.getFirst("timeStamp");
        String sign=headers.getFirst("sign");
        String body=headers.getFirst("body");

        //实际去数据库查有没有对应的accessKey
        /*if(!ifExist){
            throw new RuntimeException("无权限");
        }*/
        if(Long.parseLong(nonce)>10000){
            return handleNoAuth(response);
        }
        //时间和当前时间不能超过5分钟
        Long currentTime=System.currentTimeMillis()/1000;
        final Long FIVE_MINUTES=60*5L;
        if((currentTime-Long.parseLong(timeStamp))>=FIVE_MINUTES){
            return handleNoAuth(response);
        }
        //实际情况是从数据库中查出secretKey
        /*String serverSign= SignUtils.genSign(body,secretKey);
        if(!sign.equals(serverSign)){
            throw new RuntimeException("无权限");
        }*/
        return null;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}