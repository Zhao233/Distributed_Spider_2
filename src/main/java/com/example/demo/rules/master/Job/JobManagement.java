package com.example.demo.rules.master.Job;

import com.example.demo.rules.master.Nodes.Node;
import com.example.demo.rules.master.Nodes.NodeManagement;
import com.example.demo.utils.kafka.Producer;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class JobManagement {
    @Autowired
    @Lazy
    NodeManagement nodeManagement;

    @Autowired
    Producer producer;

    @Autowired
    JobIdGenerator jobIdGenerator;

    //待处理列表
    public static LinkedList<Job> jobs = new LinkedList<>();

    //处理列表
    public static LinkedList<Job> processing = new LinkedList<>();

    //已完成
    public static LinkedList<Job> done = new LinkedList<>();

    // job_id 与 job的映射关系
    public static HashMap<String, Job> index_map_processing = new HashMap<String, Job>();

    public static ReentrantLock takeLock_job = new ReentrantLock();

    public void add_job_to_processing(Job job, Node node){
        processing.add(job);
        index_map_processing.put(job.id, job);
        node.add_processing_job_id(job.id);
    }

    public void remove_job_from_processing(Job job){
        //从处理列表中移除id
        processing.remove(job);

        //移除映射关系
        index_map_processing.remove(job.id );

        //移除对应节点保存的待处理的列表
        Node node = nodeManagement.get_node_by_machine_id( job.machine_id );
        node.remove_processing_job_id(job.id);
    }

    public void complete_job(Job job){
        remove_job_from_processing(job);

        done.add(job);
    }

    //在任务开始时，将需要处理的任务的index范围加到待处理列表中
    public void add_jobs(int index_start, int index_end){
        log.info("-------------- "+ "init job" +" --------------");

        int start = index_start, end = index_start+1;

        while(end <= index_end){
            if( end % 10 == 0 || end >= index_end ){
                Job job = new Job(start, end, "", Job.STATUS_UNALLOCATED);
                job.id = String.valueOf(jobIdGenerator.getGeneratID());

                jobs.add(job);

                start = end + 1;
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

            if (NodeManagement.nodes.isEmpty()){
                log.info("-------------- 无可工作节点 -------------- ");
                Thread.sleep(1000);
            }

            //随机挑选一个结点，发送任务
            //改进：应根据结点状态，忙碌情况来分配任务
            Node node = nodeManagement.random_picK_slave();

            //Node node = new Node("5");

            if (node == null){
                continue;
            }

            log.info("-------------- " + "picked node: "+ node.machine_id + "-------------- ");

            if( distribute_job_to_machine(job, node) ){
                // removeable
                Thread.sleep(1000);

                log.info("-------------- " + "distribute job to machine: "+ node.machine_id + "-------------- ");

                //将任务从任务列表中移除，并放入处理中的队列

                add_job_to_processing(jobs.pop(), node);
            } else {
                continue;
            }
        }
    }

    // 将具体任务分配到机器
    public boolean distribute_job_to_machine(Job job, Node node) throws KeeperException, InterruptedException {
        Gson gson = new Gson();

        // 将处理结点的id赋予job
        job.machine_id = node.getMachine_id();

        String string_job = gson.toJson(job);

            //再次判断机器是否存活，存活后将发出指令
            //缺点：若发送后died？
            if( nodeManagement.is_slave_alive(node.getMachine_id()) ){
                log.info("-------------- " + "send job: " + string_job + " to: " + node.machine_id + " --------------");

                producer.send_job("job_"+node.getMachine_id(), string_job);

                log.info("-------------- " + "send job: " + string_job + " complete " + " --------------");

                return true;
            }

        return false;

    }

    // 接收slave发送的任务完成情况
    public void get_complement_info(String job_id){
        log.info("-------------- job_id: "+ job_id + " 任务完成" +" --------------");
        Job job = index_map_processing.get(job_id);

        if(job != null){
            complete_job(job);
            log.info("-------------- job_id: "+ job_id + "从待处理中移除" +" --------------");

            return;
        }

        log.info("-------------- 历史数据 job_id: "+ job_id + "从待处理中移除" +" --------------");
    }

    // 将对应的node的job从等待结果的列表中移除，重新加入待处理列表
    public void remove_jobs_from_job_list(Node node){

        while(!node.is_processing_job_id_list_empty()){
            String job_id = String.valueOf(node.processing_job_id_list.getFirst());

            Job job = index_map_processing.get(job_id);

            remove_job_from_processing(job);

            log.info("-------------- 从等待结果序列中移除任务: " + job.id + "; machine_id: " + node.machine_id +" --------------");
        }
    }

    // 重新将job添加到待处理列表中
    public void re_distribute_jobs(LinkedList<String> job_ids) {
        for(String id : job_ids) {
            Job job = index_map_processing.get(id);
            jobs.add(job);

            log.info("-------------- 任务重分配: " + job.id + " --------------");
        }
    }
}
