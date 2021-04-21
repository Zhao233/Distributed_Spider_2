package com.example.demo.utils.kafka;

import com.example.demo.rules.Rules;
import com.example.demo.rules.master.Job.JobManagement;
import com.example.demo.rules.slave.Job.Job;
import com.example.demo.rules.slave.Job.JobHandler;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@DependsOn(value = "kafkaTopicConfig")
public class Consumer {
    @Autowired
    JobHandler jobHandler;

    @Autowired
    JobManagement jobManagement;

    // 每台机器都监听发向自己的Job，接收后，将消息发送到自己任务的待处理队列
    // topic = "job_<machine_id>"

    Gson gson = new Gson();

//    @KafkaListener(topics = "#{'${topic}'}")
//    public void listen(ConsumerRecord<?, ?> record) {
//        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//        if (kafkaMessage.isPresent()) {
//            Object message = kafkaMessage.get();
//
//            log.info("------------------ get job =" + message.toString());
//
//            if ( Rules.current_rule == Rules.RUlE_MASTER ){
//                String job_id = message.toString();
//
//                jobManagement.get_complement_info(job_id);
//
//                return;
//            }
//
//            if( Rules.current_rule == Rules.RUlE_SALVE ){
//                Job job = gson.fromJson(message.toString(), Job.class);
//
//                jobHandler.get_job_from_master(job);
//
//                return;
//            }
//        }
//    }
}