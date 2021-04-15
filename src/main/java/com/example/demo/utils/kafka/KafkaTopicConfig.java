package com.example.demo.utils.kafka;

import com.example.demo.rules.Rules;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.stream.Collectors;

@Dep
@Configuration
public class KafkaTopicConfig implements InitializingBean {
    @Value("${machine_id}")
    public String machine_id;

    @Override
    public void afterPropertiesSet() {
        if( Rules.current_rule == Rules.RUlE_SALVE){
            System.setProperty("topic", "job_"+machine_id);
        }

        if( Rules.current_rule == Rules.RUlE_MASTER ){
            System.setProperty("topic", "job_master");
        }
    }
}