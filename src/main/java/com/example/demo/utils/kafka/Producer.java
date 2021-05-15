package com.example.demo.utils.kafka;

import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class Producer {
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, String content){
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, content);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //发送失败的处理
                log.info(topic + " - 生产者 发送消息失败：" + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                //成功的处理
                log.info(topic + " - 生产者 发送消息成功：" + stringObjectSendResult.toString());
            }
        });
    }

    public void send_job(String machine_id, String job_String){
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(machine_id, job_String);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //发送失败的处理
                log.info(machine_id + " =============================== 生产者 发送消息失败：" + throwable.getMessage()+" =============================== ");
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                //成功的处理
                log.info(machine_id + " =============================== 生产者 发送消息成功：" + stringObjectSendResult.toString() +" =============================== ");
            }
        });
    }
}
