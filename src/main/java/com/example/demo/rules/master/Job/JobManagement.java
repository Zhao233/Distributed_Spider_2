package com.example.demo.rules.master.Job;

import com.example.demo.rules.master.Nodes.Node;
import com.example.demo.rules.master.Nodes.NodeManagement;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class JobManagement {
    @Autowired
    NodeManagement nodeManagement;

    LinkedList<Job> jobs;
    LinkedList<Job> processing = new LinkedList<>();

    public void test(){
        jobs = new LinkedList<>();

        for(int i = 0; i < 20; i++){
            jobs.add(new Job(i, i+3));

            i+=3 ;
        }
    }

    public void register_jobs(List<Job> jobs){
        this.jobs.addAll(jobs);
    }

    public void distribute_jobs() throws KeeperException, InterruptedException {
        List<Node> nodes = nodeManagement.get_all_slaves();

        while(!jobs.isEmpty()){
            Job job = jobs.getFirst();


        }
    }

    public void distribute_job_to_machine(Job job, int machine_id){

    }
}
