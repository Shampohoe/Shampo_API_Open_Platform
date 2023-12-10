package com.shampo.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shampo.project.service.impl.UserInterfaceInfoServiceImpl;
import com.shampo.shampocommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * ClassName:RabbitMqListener
 * Package:com.shampo.project.service.impl.inner
 * Description: MQ消费者
 *
 * @Author kkli
 * @Create 2023/12/7 15:26
 * #Version 1.1
 */
@Component
@Slf4j
public class RabbitMqListener {
    @Resource
    UserInterfaceInfoServiceImpl userInterfaceInfoService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.shampo1"),
            exchange = @Exchange(name = "shampo.direct", type = ExchangeTypes.DIRECT),
            key = {"sql", "update"}
    ))
    public void listenDirectQueue1(UserInterfaceInfo msg){
        System.out.println("消费者接收到direct.queue1的消息：【" + msg + "】");
        long userId = msg.getUserId();
        long interfaceInfoId = msg.getInterfaceInfoId();
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userId);
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.setSql("leftNum=leftNum-1,totalNum=totalNum+1");
        userInterfaceInfoService.update(updateWrapper);
    }
}
