package com.example.demo.rules.master.Job;

import com.example.demo.rules.master.Nodes.Node;
import com.example.demo.rules.master.Nodes.NodeManagement;
import com.example.demo.utils.kafka.Producer;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class JobManagement {
    @Autowired
    NodeManagement nodeManagement;

    @Autowired
    Producer producer;

    @Autowired
    JobIdGenerator jobIdGenerator;

    //待处理列表
    public static LinkedList<Job> jobs = new LinkedList<>();

    //处理列表
    public static LinkedList<Job> processing = new LinkedList<>();

    //记录id与
    public static HashMap<String, Job> index_map = new HashMap<>();

    //在任务开始时，将需要处理的任务的index范围加到待处理列表中
    public void add_jobs(int index_start, int index_end){
        log.info("-------------- "+ "init job" +" --------------");

        int start = index_start, end = index_start+1;

        while(end <= index_end){
            if( end % 10 == 0 || end >= index_end ){
                Job job = new Job(start, end, -1, Job.STATUS_UNALLOCATED);
                job.id = jobIdGenerator.getGeneratID();

                jobs.add(job);

                start = end+1;
            }

            end++;
        }

        log.info("-------------- "+ "init job complete" +" --------------");
    }

    // 分配任务
    @Async("asyncServiceExecutor")
    public void distribute_jobs() throws KeeperException, InterruptedException {

        while(!jobs.isEmpty()){
            Job job = jobs.getFirst();

            //随机挑选一个结点，发送任务
            //改进：应根据结点状态，忙碌情况来分配任务
            Node node = nodeManagement.random_picK_slave();

            //Node node = new Node("5");

            if (node == null){
                continue;
            }

            log.info("-------------- " + "picked node: "+ node.machine_id + "-------------- ");

            if( distribute_job_to_machine(job, node) ){

                log.info("-------------- " + "distribute job to machine: "+ node.machine_id + "-------------- ");

                //将任务从任务列表中移除，并放入处理中的队列
                processing.add(jobs.pop());
                index_map.put(String.valueOf(job.id), job);
            } else {
                continue;
            }
        }
    }

    // 将具体任务分配到机器
    public boolean distribute_job_to_machine(Job job, Node node) throws KeeperException, InterruptedException {
        Gson gson = new Gson();

        String string_job = gson.toJson(job);

            //再次判断机器是否存活，存活后将发出指令
            //缺点：若发送后died？
            if( nodeManagement.is_slave_alive(node.getMachine_id()) ){
                // 将处理结点的id赋予job
                job.machine_id = Integer.parseInt(node.getMachine_id());

                log.info("-------------- " + "send job: " + string_job + " to: " + node.machine_id + " --------------");

                producer.send_job(node.getMachine_id(), string_job);

                log.info("-------------- " + "send job: " + string_job + " complete " + " --------------");

                return true;
            }

        return false;

    }

    // 接收slave发送的任务完成情况
    public void get_complement_info(String job_id){
        log.info("-------------- job_id: "+ job_id + " complete" +" --------------");
        Job job = index_map.get(job_id);

        processing.remove(job);
        index_map.remove(job_id);

        log.info("-------------- job_id: "+ job_id + " remove from processing list" +" --------------");
    }
}
