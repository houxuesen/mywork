package cn.hxs.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author hxs
 * @Date 2020/12/7 10:59
 * @Description
 * @Version 1.0
 */
@Data
public class Security {
    private  String username;

    private  String password;

    private  List<String> roles = new ArrayList<>();

    public Security() {
    }

    public Security(String username, String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

}
