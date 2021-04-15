package com.example.demo;

import com.example.demo.rules.Rules;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import sun.tools.jar.CommandLine;

@Component
public class StartPingService  {

    @Autowired
    Rules rules;

//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        rules.register_machine();
//    }
}