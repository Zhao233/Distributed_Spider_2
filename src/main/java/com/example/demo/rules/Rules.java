package com.example.demo.rules;

import org.springframework.stereotype.Component;

@Component
public class Rules {
    public static final int RUlE_MASTER = 1;
    public static final int RUlE_SALVE = 2;
    public static final int RUlE_OBSERVER = 3;

    // 默认角色为observer
    public int current_rule = 3;

    public void changeRule(int rule){
        current_rule = rule;
    }

    // 判断当前系统是否存在master
    public boolean isMasterExsistInSystem(){

        return false;
    }
}
