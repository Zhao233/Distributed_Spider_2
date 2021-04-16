package com.example.demo.rules.master.Nodes;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class NodeManagement {
    @Autowired
    ZooKeeper zkClient;

    @Value("${zookeeper.nodes_path}")
    public String nodes_path;

    public Random random;

    // 所有可工作的slave
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

    public Node random_picK_slave(){
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

}
