package com.example.demo.utils.kafka;

import com.example.demo.rules.Rules;
import com.example.demo.rules.master.Nodes.NodeManagement;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig implements InitializingBean {
    @Value("${machine_id}")
    public String machine_id;



    @Autowired
    Rules rules;



    @Override
    public void afterPropertiesSet() throws KeeperException, InterruptedException {
        //可以视作在机器初始化的操作
        //如果当前角色为观察者（未分配工作）则先进行注册工作
        if( Rules.current_rule == Rules.RUlE_OBSERVER){
            rules.register_machine();
        }

        // 如果角色为slave，监测kafka中 job_<machine_id>的信息
        if( Rules.current_rule == Rules.RUlE_SALVE){
            System.setProperty("topic", "job_"+machine_id);


        }

        if( Rules.current_rule == Rules.RUlE_MASTER ){
            System.setProperty("topic", "job_master");
        }


    }
}