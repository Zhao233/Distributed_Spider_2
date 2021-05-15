package com.example.demo.rules.master.Nodes;

import com.example.demo.rules.master.Job.JobManagement;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class NodeManagement {
    @Autowired
    ZooKeeper zkClient;

    @Autowired
    JobManagement jobManagement;

    @Value("${zookeeper.nodes_path}")
    public String nodes_path;

    // 所有可工作的slave
    // 需要加锁
    public static LinkedList<Node> nodes = new LinkedList<>();

    // 记录所有slave的machine_id与在工作slave队列中的node的对应关系
    public static HashMap<String, Node> nodes_index_map = new HashMap<>();

    public LinkedList<Node> get_all_slaves() throws KeeperException, InterruptedException {
        List<String> child_list = zkClient.getChildren(nodes_path,null);
        LinkedList<Node> nodes = new LinkedList<>();

         for (String temp : child_list){
             byte[] data = zkClient.getData(nodes_path+"/"+temp,null, null);

             String data_ = new String(data);
             if(data_.equals("slave")){
                 nodes.add(new Node(temp));
             }
         }

         return nodes;
    }

    public boolean is_slave_alive(String machine_id) throws KeeperException, InterruptedException {
        return zkClient.exists(nodes_path+"/"+machine_id, null) != null;
    }

    public void addWatcher(String path){
        CustomWatcher watcher = new CustomWatcher(path);

        try {
            zkClient.getChildren(path, watcher);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Node random_picK_slave(){
        Random random = new Random();

        if(nodes.size() == 0){
            return null;
        }

        return nodes.get(random.nextInt(nodes.size()));
    }

    //将一个slave添加到工作者队列中
    public void addSlave(String machine_id){
        Node node = new Node(machine_id);

        nodes.add(node);
        nodes_index_map.put(machine_id, node);
    }

    public void removeSlave(String machine_id){
        nodes.remove(nodes_index_map.get(machine_id));
        nodes_index_map.remove(machine_id);
    }

    //监听器
    class CustomWatcher implements Watcher{
        String path;

        CustomWatcher(String path){
            this.path = path;
        }

        @Override
        @Synchronized
        public void process(WatchedEvent watchedEvent) {
            CustomWatcher watcher = new CustomWatcher(path);

            List<String> child;

            log.info("=============================== event create ==========================");

            try {
                child = zkClient.getChildren(path, watcher);
                LinkedList<Node> temp_nodes = (LinkedList<Node>) nodes.clone();

                HashMap<String, String> map = new HashMap<>();

                for(String temp : child){
                    map.put(temp, temp);
                }

                for(Node node : temp_nodes){
                    String ret = map.get(node.getMachine_id());

                    //node被删除了
                    // 1. 从节点列表中删除对应节点
                    // 2. 将对应的任务列表中的任务重新分配 （未完成）
                    if(ret == null){
                        // 1
                        nodes.remove(node);
                        nodes_index_map.remove(node.getMachine_id());

                        log.info("=============================== 删除节点 " + node.getMachine_id() + "==========================");

                        // 2
                        LinkedList<String> job_ids = (LinkedList<String>) node.getProcessing_job_id_list().clone();

                        jobManagement.remove_jobs_from_job_list(node);
                        jobManagement.re_distribute_jobs(job_ids);

                        log.info("=============================== 节点任务重分配 " + node.getMachine_id() + "==========================");

                    }
                }

                for(String temp : child){
                    Node node_ = nodes_index_map.get(temp);

                    //新增加了node
                    if(node_ == null){
                        Node node = new Node(temp);

                        nodes.add( node );
                        nodes_index_map.put(temp, node);

                        log.info("=============================== 新增节点 " + node.getMachine_id() + "==========================");
                    }
                }

            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

