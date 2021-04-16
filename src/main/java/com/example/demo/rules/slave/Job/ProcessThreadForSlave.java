package com.example.demo.rules.slave.Job;

import com.example.demo.db.WeiBoDao;
import com.example.demo.rules.slave.Spider.SpiderContent;
import com.example.demo.utils.kafka.Producer;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ProcessThreadForSlave {
    @Autowired
    JobHandler handler;

//    @Autowired
//    WeiBoDao weiBoDao;

    @Autowired
    Producer producer;

    Gson gson = new Gson();

    @Async("asyncServiceExecutor")
    // 时时检查未处理队列，如果存在未处理数据，处理
    public void process_jobs_in_un_processed_list() throws IOException {
        // 判断是否为空
        while(!JobHandler.un_processed.isEmpty()){
            Job job = JobHandler.un_processed.pop();

            log.info("process job: " + job.id);
            Job_done done = handler.decode_job_and_send_to_spider(job);

            //将数据添加到已完成队列
            JobHandler.processed.add(done);

            //将job移出已完成队列
            JobHandler.un_processed.remove(job);
        }
    }

    @Async("asyncServiceExecutor")
    // 时时检查已爬取数据，并将完成的数据保存到数据库，通知master更新完成情况
    public void process_processed_jobs(){
        while( !JobHandler.processed.isEmpty() ){
            Job_done done = JobHandler.processed.pop();

            //将一个已完成任务中的爬取内容保存
            for(SpiderContent temp : done.getContents()) {
                //weiBoDao.save(temp);
            }

            //通知master更新完成情况
            producer.send("job_"+done.job.machine_id, String.valueOf(done.job.id));
        }
    }

}
