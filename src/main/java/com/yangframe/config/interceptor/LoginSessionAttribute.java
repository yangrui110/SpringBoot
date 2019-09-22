package com.yangframe.config.interceptor;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LoginSessionAttribute {

    private String loginId;
    private Map<String,Object> loginInfo;
    private List<String> roles;
    private List<String> permissions;

    public boolean hasPermissions(String permission){
        for(String per: permissions){
            if(permission.equals(per))
                return true;
        }
        return false;
    }

    public boolean hasRole(String role){
        for(String r : roles){
            if(role.equals(r))
                return true;
        }
        return true;
    }
}
