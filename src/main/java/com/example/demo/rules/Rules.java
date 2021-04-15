package com.example.demo.rules;

import com.example.demo.utils.zookeeper.lock.Imp.BaseDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.System.exit;

@Component
@Slf4j
public class Rules {
    @Autowired
    BaseDistributedLock lock;

    @Autowired
    ZooKeeper zkClient;

    public static final int RUlE_MASTER = 1;
    public static final int RUlE_SALVE = 2;
    public static final int RUlE_OBSERVER = 3;

    // 默认角色为observer
    public static int current_rule = 3;
    public String current_path = "";

    @Value("${machine_id}")
    public String machine_id;

    @Value("${zookeeper.nodes_path}")
    public String nodes_path;

    @Value("${zookeeper.master_path}")
    private String master_path;

    public static void changeRule(int rule){
        System.setProperty("rule", String.valueOf(rule));

        current_rule = rule;
    }

    public boolean check_is_machine_id_exist(){
        //List<String> ids = zkClient.getChildren("");
        return false;
    }

    public void register_machine() throws KeeperException, InterruptedException {
        //检查machine_id是否唯一
        if(check_is_machine_id_exist()){
            log.info("machine_id已存在!!!");
            exit(-1);
        }

        current_path = zkClient.create(nodes_path+"/"+machine_id, machine_id.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        System.out.println("register to nodes, machine_id: "+machine_id);

        try {
            elect_leader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断当前系统是否存在master
    public boolean is_master_exist() throws KeeperException, InterruptedException {
        List<String> master = zkClient.getChildren(master_path, null);

        if( master.isEmpty() ){
            return false;
        } else {
            return true;
        }
    }

    public void elect_leader() throws Exception {
        lock.acquire();

        if (is_master_exist()){
            changeRule(RUlE_SALVE);
        } else {
            zkClient.create(master_path+"/"+machine_id, machine_id.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

            changeRule(RUlE_MASTER);
        }

        lock.release();

        System.out.println("elect complete ! i'm " + current_rule);

        switch (current_rule){
            case RUlE_MASTER:
                zkClient.setData(nodes_path+"/"+machine_id, "master".getBytes(),-1);

                break;

            case RUlE_SALVE:
                zkClient.setData(nodes_path+"/"+machine_id, "slave".getBytes(),-1);
                break;
        }
    }
}
