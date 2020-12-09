package cn.hxs.model;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Component;

import java.net.InetAddress;


/**
 * @Author hxs
 * @Date 2020/12/4 18:34
 * @Description
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "acme")
@ConstructorBinding
public class AcmeProperties {
    private  Boolean enabled;

    private  InetAddress remoteAddress;

    private  Security security = new Security();

    public AcmeProperties() {
    }

    public AcmeProperties(Boolean enabled, InetAddress remoteAddress,Security security) {
        this.enabled = enabled;
        this.remoteAddress = remoteAddress;
        this.security = security;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
