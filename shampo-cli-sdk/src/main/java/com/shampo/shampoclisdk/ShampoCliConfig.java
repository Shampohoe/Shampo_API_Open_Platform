package com.shampo.shampoclisdk;

import com.shampo.shampoclisdk.client.ShampoClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName:ShampoApiConfig
 * Package:com.shampo.shampoclisdk
 * Description:
 *
 * @Author kkli
 * @Create 2023/10/27 0:59
 * #Version 1.1
 */
@Configuration
@ConfigurationProperties("shampo.client")
@Data
@ComponentScan
public class ShampoCliConfig {
    private String accessKey;
    private String secretKey;
    @Bean
    public ShampoClient shampoClient(){
        return new ShampoClient(accessKey,secretKey);
    }

}
