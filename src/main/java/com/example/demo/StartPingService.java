package com.example.demo;

import com.example.demo.rules.Rules;
import com.example.demo.rules.master.Job.JobManagement;
import com.example.demo.rules.master.Nodes.NodeManagement;
import com.example.demo.rules.slave.Job.ProcessThreadForSlave;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import sun.tools.jar.CommandLine;

@Component
public class StartPingService implements CommandLineRunner {

    @Autowired
    Rules rules;

    @Autowired
    JobManagement jobManagement;

    @Autowired
    ProcessThreadForSlave processThreadForSlave;

    @Autowired
    ZooKeeper zkClient;

    @Autowired
    NodeManagement nodeManagement;

    @Value("${zookeeper.nodes_path}")
    public String node_path;


    @Override
    public void run(String... args) throws Exception {
        //机器启动后，如果为master开启分配任务线程
        if(Rules.current_rule == Rules.RUlE_MASTER){
            /** ======================= zookeeper注册监听事件 ========================= */
            nodeManagement.addWatcher(node_path);

            /** ======================= 初始化任务 ========================= */
            //jobManagement.add_jobs(0, 100);

            /** ======================= 分发任务 ========================= */
            //jobManagement.distribute_jobs();


        }

        //机器启动后，对slave的任务分配
        if(Rules.current_rule == Rules.RUlE_SALVE){
            processThreadForSlave.process_jobs_in_un_processed_list();
            processThreadForSlave.process_processed_jobs();
        }
    }

//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        rules.register_machine();
//    }
}