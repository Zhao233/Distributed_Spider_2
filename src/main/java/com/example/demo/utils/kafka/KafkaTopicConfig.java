package com.example.demo.utils.kafka;

import com.example.demo.rules.Rules;
import org.apache.zookeeper.KeeperException;
import org.junit.Rule;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.xml.ws.Action;

@Configuration
public class KafkaTopicConfig implements InitializingBean {
    @Value("${machine_id}")
    public String machine_id;

    @Autowired
    Rules rules;

    @Override
    public void afterPropertiesSet() throws KeeperException, InterruptedException {
        //可以视作在机器初始化的操作
        if( Rules.current_rule == Rules.RUlE_OBSERVER){
            rules.register_machine();
        }

        if( Rules.current_rule == Rules.RUlE_SALVE){
            System.setProperty("topic", "job_"+machine_id);
        }

        if( Rules.current_rule == Rules.RUlE_MASTER ){
            System.setProperty("topic", "job_master");
        }


    }
}