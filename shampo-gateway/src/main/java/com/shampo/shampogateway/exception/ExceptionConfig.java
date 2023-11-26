package com.shampo.shampogateway.exception;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;
/**
 * ClassName:ExceptionConfig
 * Package:com.shampo.shampogateway.exception
 * Description:
 *
 * @Author kkli
 * @Create 2023/10/31 21:14
 * #Version 1.1
 */
@Configuration
public class ExceptionConfig {

    /**
     * 自定义异常处理[@@]注册Bean时依赖的Bean，会从容器中直接获取，所以直接注入即可
     */
    @Primary
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                                             ServerCodecConfigurer serverCodecConfigurer ,
                                                             CustomWebExceptionHandler customWebExceptionHandler) {
        customWebExceptionHandler.setViewResolvers(viewResolversProvider.getIfAvailable(Collections::emptyList));
        customWebExceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        customWebExceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return customWebExceptionHandler;
    }
}
