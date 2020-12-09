package cn.hxs.control;

import cn.hxs.model.AcmeProperties;
import cn.hxs.model.AnotherAcmeProperties;
import cn.hxs.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author hxs
 * @Date 2020/12/4 16:51
 * @Description
 * @Version 1.0
 */
@RestController
@EnableConfigurationProperties({UserModel.class,AcmeProperties.class})
public class YmlControl {

    @Autowired
    private UserModel userModel;

    @Autowired
    private AcmeProperties acmeModel;

    @Autowired
    private AnotherAcmeProperties anotherAcmeProperties;

    @RequestMapping("/hello")
    public String hello(){
        return "hello world11111";
    }

    @RequestMapping("/getUser")
    public UserModel getUser(){
        return userModel;
    }

    @RequestMapping("/getAcmeModel")
    public AcmeProperties getAcmeModel(){
        return acmeModel;
    }

    @RequestMapping("/getAnotherAcmeProperties")
    public AnotherAcmeProperties getAnotherAcmeProperties(){
        return anotherAcmeProperties;
    }
}
