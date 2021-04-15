package com.example.demo.rules.slave.Job;

import com.example.demo.rules.slave.Spider.Spider;
import com.example.demo.rules.slave.Spider.SpiderContent;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class JobHandler {
    @Autowired
    Spider spider;

    //未完成队列
    public static LinkedList<Job> un_processed = new LinkedList();
    //已完成队列，任务完成后，将已完成的任务提交数据库和通知master
    public static LinkedList<Job_done> processed = new LinkedList();

    //处理未处理数据，并将数据发送到爬虫端，返回爬取后的数据
    //@Async("asyncServiceExecutor")
    public Job_done decode_job_and_send_to_spider(Job job) throws IOException {
        LinkedList<SpiderContent> ret = spider.crawling_data(job.start, job.end);

        Job_done done = new Job_done(job, ret);

        return done;
    }
}
