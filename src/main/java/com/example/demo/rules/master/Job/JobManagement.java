package com.example.demo.rules.master.Job;

import com.example.demo.rules.master.Nodes.Node;
import com.example.demo.rules.master.Nodes.NodeManagement;
import com.example.demo.utils.kafka.Producer;
import com.google.gson.Gson;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Component
public class JobManagement {
    @Autowired
    NodeManagement nodeManagement;

    @Autowired
    Producer producer;

    LinkedList<Job> jobs;
    LinkedList<Job> processing = new LinkedList<>();

    public void register_jobs(List<Job> jobs){
        this.jobs.addAll(jobs);
    }

    public void distribute_jobs() throws KeeperException, InterruptedException {
        Random random = new Random();
        LinkedList<Node> nodes = nodeManagement.get_all_slaves();

        while(!jobs.isEmpty()){
            Job job = jobs.getFirst();

            //随机挑选一个结点，发送任务
            //改进：应根据结点状态，忙碌情况来分配任务
            if( distribute_job_to_machine(job, nodes.get(random.nextInt(nodes.size())) ) ){
                //将任务从任务列表中移除，并放入处理中的队列
                processing.add(jobs.pop());
            } else {
                continue;
            }
        }
    }

    public boolean distribute_job_to_machine(Job job, Node node){
        Gson gson = new Gson();

        String string = gson.toJson(job);

        try {
            //再次判断机器是否存活，存活后将发出指令
            //缺点：若发送后died？
            if( nodeManagement.is_slave_alive(node.getMachine_id()) ){

                producer.sen_job(node.getMachine_id(), node.getMachine_id());

                return true;
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;

    }
}
