package com.example.demo;

import com.example.demo.rules.Rules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartPingService implements CommandLineRunner {

    @Autowired
    Rules rules;

    @Override
    public void run(String... args) throws Exception {
        // TODO Auto-generated method stub
        rules.register_machine();

        //System.out.println("hhhhhhhhhhhhh");
    }

}