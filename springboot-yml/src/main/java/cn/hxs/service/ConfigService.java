package cn.hxs.service;

import cn.hxs.model.AnotherAcmeProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * @Author hxs
 * @Date 2020/12/7 14:39
 * @Description
 * @Version 1.0
 */
@Service
public class ConfigService {
    @Bean
    @ConfigurationProperties("acme")
    public AnotherAcmeProperties getAnotherAcmeProperties(){
        return new AnotherAcmeProperties();
    }
}
