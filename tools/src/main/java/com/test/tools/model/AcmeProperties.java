package com.test.tools.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Component;

/**
 * @Author hxs
 * @Date 2020/12/4 18:34
 * @Description
 * @Version 1.0
 */
@Component
@ConstructorBinding
@ConfigurationProperties(prefix = "acme")
public class AcmeProperties {
    private final boolean enabled;

    public AcmeProperties(boolean enabled) {
        this.enabled = enabled;
    }
}
