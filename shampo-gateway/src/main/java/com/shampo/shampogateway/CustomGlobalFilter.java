package com.shampo.shampogateway;


import com.shampo.shampoclisdk.utils.SignUtils;
import com.shampo.shampocommon.model.entity.InterfaceInfo;
import com.shampo.shampocommon.model.entity.User;
import com.shampo.shampocommon.service.InnerInterfaceInfoService;
import com.shampo.shampocommon.service.InnerUserInterfaceInfoService;
import com.shampo.shampocommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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
    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference(timeout = 20000,retries = 0)
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;
    private static final List<String> IP_WHITE_LIST= Arrays.asList("127.0.0.1");
    private static final String INTERFACE_HOST="http://localhost:8123";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. 用户发送请求到API网关
        // 2. 请求日志
        ServerHttpRequest request= exchange.getRequest();
        String path = INTERFACE_HOST+request.getPath().value();
        String method = request.getMethod().toString();
       /* log.info("请求唯一标识"+request.getId());
        log.info("请求路径"+path);
        log.info("请求方法"+method);
        log.info("请求参数"+request.getQueryParams());*/
        String hostString = request.getLocalAddress().getHostString();
        /*log.info("请求来源地址"+hostString);
        log.info("请求来源地址"+request.getRemoteAddress());*/
        // 3. （黑白名单）
        ServerHttpResponse response = exchange.getResponse();
        if(!IP_WHITE_LIST.contains(hostString)){
            log.info("0");
            return handleNoAuth(response);
        }
        // 4. 用户鉴权（判断 ak、sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        log.info("body:"+body);
        // 去数据库中查是否已分配给用户
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            log.info("1");
            return handleNoAuth(response);
        }
        if (Long.parseLong(nonce) > 10000L) {
            log.info("2");
            return handleNoAuth(response);
        }
        // 时间和当前时间不能超过 5 分钟
        Long currentTime = System.currentTimeMillis() / 1000;
        final Long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            log.info("3");
            return handleNoAuth(response);
        }
        //从数据库中查出 secretKey
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.genSign(body, secretKey);
        if (sign == null || !sign.equals(serverSign)) {
            log.info("4");
            return handleNoAuth(response);
        }
        // 5. 请求的模拟接口是否存在，以及请求方法是否匹配
        // 从数据库查询模拟接口是否存在，以及请求方法是否匹配，（校验请求参数）
        // 因为网关项目没有引入MyBatis等操作数据库的类库，如果该操作比较复杂，可以由backend增删改查项目提供接口，直接调用接口，不再重复写逻辑
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            log.info("5");
            return handleNoAuth(response);
        }
        // 6. 请求转发、调用模拟接口
        // 7. 响应日志
        Mono<Void> filter=handleResponse(exchange, chain,interfaceInfo.getId(),invokeUser.getId());//异步调用
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
    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,long interfaceInfoId,long userId) {
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
                            log.info(exchange.getResponse().getStatusCode().toString());
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            if(exchange.getResponse().getStatusCode()==HttpStatus.INTERNAL_SERVER_ERROR){
                                throw new RuntimeException("后端服务模块调用异常");
                            }
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                        // 8. todo 调用成功，接口调用次数 + 1 invokeCount
                                        try{
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId,userId);
                                        }catch(Exception e){
                                            log.error("invokeCount error",e);
                                            throw new RuntimeException("接口统计调用失败");
                                        }

                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存

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
                return  result;
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
           return handleInvokeError(exchange.getResponse());
            //return chain.filter(exchange);
        }
    }



    @Override
    public int getOrder() {
        return -1;
    }
}