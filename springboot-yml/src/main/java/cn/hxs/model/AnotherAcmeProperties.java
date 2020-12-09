package cn.hxs.model;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.net.InetAddress;


/**
 * @Author hxs
 * @Date 2020/12/4 18:34
 * @Description
 * @Version 1.0
 */
@Data
public class AnotherAcmeProperties {
    private  Boolean enabled;

    private  InetAddress remoteAddress;

    @Override
    public String toString() {
        return super.toString();
    }
}
