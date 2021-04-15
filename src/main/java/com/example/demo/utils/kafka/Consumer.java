package com.example.demo.utils.kafka;

import com.example.demo.rules.slave.Job.Job;
import com.example.demo.rules.slave.Job.JobHandler;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@DependsOn(value = "kafkaTopicConfig")
public class Consumer {

    // 每台机器都监听发向自己的Job，接收后，将消息发送到自己任务的待处理队列
    // topic = "job_<machine_id>"

    Gson gson = new Gson();

    @KafkaListener(topics = "#{'${topic}'}")
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {

            Object message = kafkaMessage.get();

            log.info("------------------ get job =" + message.toString());

            Job job = gson.fromJson(message.toString(), Job.class);

            JobHandler.un_processed.add(job);
        }
    }
}