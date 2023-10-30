package com.shampo.shampo_interface.aop;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ClassName:invokeCountAop
 * Package:com.shampo.shampo_interface.aop
 * Description:
 *
 * @Author kkli
 * @Create 2023/10/29 14:57
 * #Version 1.1
 */

/*
调用次数切面:实际上应该在网关层实现
* */

@RestControllerAdvice
public class invokeCountAop {

    //定义切莫触发时机（什么时候执行方法）controller接口的方法执行成功后执行该方法


}
